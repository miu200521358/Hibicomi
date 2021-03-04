package jp.comfycolor.hibicomi.launcher;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.google.gson.Gson;

import jp.comfycolor.hibicomi.bean.info.BookNotAdInfoBean;
import jp.comfycolor.hibicomi.bean.info.GenreInfoBean;
import jp.comfycolor.hibicomi.bean.setting.SettingBean;
import jp.comfycolor.hibicomi.utils.api.ServerApiUtils;
import jp.comfycolor.hibicomi.utils.file.MyFileUtils;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class FreeTwitterLauncher extends BaseLauncher {

	protected URLCodec codec;
	protected Twitter twitter;

	public FreeTwitterLauncher(SettingBean setting) {
		super(setting);

		codec = new URLCodec("UTF-8");

		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(false)
				.setOAuthConsumerKey(setting.getIndividualSetting().getTwitterSetting().getConsumerKey())
				.setOAuthConsumerSecret(setting.getIndividualSetting().getTwitterSetting().getConsumerSecret())
				.setOAuthAccessToken(setting.getIndividualSetting().getTwitterSetting().getAccessToken())
				.setOAuthAccessTokenSecret(setting.getIndividualSetting().getTwitterSetting().getAccessTokenSecret());
		cb.setTweetModeExtended(true);

		TwitterFactory tf = new TwitterFactory(cb.build());
		twitter = tf.getInstance();
	}

	@Override
	public void execute() throws Exception {
		// 無料情報ツイート
		tweetFree();

		// ふぁぼ
		tweetFavorite();
	}

	protected boolean tweetFree() throws TwitterException, EncoderException {
		// 告知書籍情報取得
		String serverUrl = StringUtils.replace(setting.getWeb().getBookNotAdUrl(), "{bookGenres}",
				StringUtils.join(setting.getIndividualSetting().getTargetGenreList(), ","));

		String responseData = ServerApiUtils.requestGetServerApi(setting, serverUrl);

		Gson gson = MyFileUtils.createBookInfoGson();
		BookNotAdInfoBean bookInfo = gson.fromJson(responseData, BookNotAdInfoBean.class);

		logger.debug("■Twitter対象: " + bookInfo.toString());

		// 文言生成
		String message = setting.getIndividualSetting().getTwitterSetting().getTemplate();
		message = StringUtils.replace(message, "{title}", bookInfo.getBook().getTitle());
		message = StringUtils.replace(message, "{title_number}",
				StringUtils.isEmpty(bookInfo.getBook().getTitle_number()) ? ""
						: "(" + bookInfo.getBook().getTitle_number() + ")");
		message = StringUtils.replace(message, "{author}", bookInfo.getBook().getAuthor());
		message = StringUtils.replace(message, "{catchcopy}", bookInfo.getBook().getCatchcopy());
		message = StringUtils.replace(message, "{site}", bookInfo.getSite().getSite_name());

		StringBuffer genreBuf = new StringBuffer();
		if (bookInfo.getGenres().size() > 0) {
			for (GenreInfoBean genre : bookInfo.getGenres()) {
				genreBuf.append(" #");
				genreBuf.append(genre.getGenre_name());
				genreBuf.append("まんが");
			}
		}
		message = StringUtils.replace(message, "{genres}", genreBuf.toString() + " ");
		message = StringUtils.replace(message, "{free_end}",
				bookInfo.getBook_site().getFree_end_datetime().format(MyFileUtils.TWITTER_YM_FORMATTER));

		// サイトへのリンク
		String titleUrl = "https://hibicomi.info/books?title=" + codec.encode(bookInfo.getBook().getTitle());

		logger.debug("■Twitter文言: " + message);
		logger.debug("■Twitter URL: " + titleUrl);

		// Twitter4Jで投稿
		Status status = twitter.updateStatus(message + titleUrl);

		logger.info("■投稿成功: " + status.getText());

		// 投稿に成功したら、そのまま送り返して更新する
		List<NameValuePair> requestParams = new ArrayList<>();
		requestParams.add(new BasicNameValuePair("data", gson.toJson(bookInfo.getBook_site())));

		return ServerApiUtils.requestPostServerApi(setting, serverUrl, requestParams);
	}

	protected boolean tweetFavorite() throws TwitterException {

		for (String query : setting.getIndividualSetting().getTwitterSetting().getQueryList()) {
			logger.debug("■ツイート検索: " + query);

			QueryResult results = twitter.search(new Query(query));
			for (Status tweet : results.getTweets()) {
				// まだふぁぼしてなければする。
				// リスト処理で既にふぁぼしている可能性があるので、取得し直してからチェック
				// ブロックされている可能性があるので、try-catchで囲んでおく
				try {
					if (!twitter.showStatus(tweet.getId()).isFavorited()) {
						twitter.createFavorite(tweet.getId());
						logger.debug("■■FAV @" + tweet.getUser().getScreenName() + " [" + tweet.getId() + "]" + " - "
								+ tweet.getText());
					} else {
						logger.debug("■■■FAVED @" + tweet.getUser().getScreenName() + " [" + tweet.getId() + "]" + " - "
								+ tweet.getText());
					}
				} catch (TwitterException e) {
					logger.warn("■■FAV失敗 @" + tweet.getUser().getScreenName() + " [" + tweet.getId() + "]", e);
				}
			}
		}

		return true;
	}

}
