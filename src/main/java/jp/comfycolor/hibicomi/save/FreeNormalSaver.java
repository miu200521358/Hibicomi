package jp.comfycolor.hibicomi.save;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import jp.comfycolor.hibicomi.bean.info.BookInfoBean;
import jp.comfycolor.hibicomi.bean.info.BookSiteInfoBean;
import jp.comfycolor.hibicomi.bean.info.SiteInfoBean;
import jp.comfycolor.hibicomi.bean.setting.SettingBean;
import jp.comfycolor.hibicomi.utils.api.ServerApiUtils;
import jp.comfycolor.hibicomi.utils.err.HibicomiFailureException;
import jp.comfycolor.hibicomi.utils.file.MyFileUtils;
import jp.comfycolor.hibicomi.utils.retry.Proc;
import jp.comfycolor.hibicomi.utils.retry.Runnable;

public class FreeNormalSaver extends BaseSaver {

	private static Logger logger = LoggerFactory.getLogger(FreeNormalSaver.class);

	protected BookSiteInfoBean bookSiteInfo;
	protected URLCodec codec;

	public FreeNormalSaver(SettingBean setting, File dataFile) throws Exception {
		super(setting, dataFile);

		Gson gson = MyFileUtils.createBookInfoGson();

		try {
			bookSiteInfo = gson.fromJson(FileUtils.readFileToString(this.dataFile, "UTF-8"), BookSiteInfoBean.class);
		} catch (JsonSyntaxException | IOException e) {
			logger.error("展開失敗: " + this.dataFile.getAbsolutePath(), e);
			throw e;
		}

		codec = new URLCodec("UTF-8");
	}

	@Override
	public boolean execute() throws Exception {
		logger.info("■SAVE "+ bookSiteInfo.getSite().toString());

		// サイト情報保存
		Proc.retry(3, new Runnable() {
			@Override
			public void run() throws Throwable {
				saveSiteInfo(bookSiteInfo.getSite());
			}
		}, 3 * 60 * 1000);

		// 書籍情報をJSONで保存する
		File localDataFile = this.dataFile;

		logger.debug("■SAVE "+ localDataFile.getAbsolutePath());

		Proc.retry(3, new Runnable() {
			@Override
			public void run() throws Throwable {
				// サーバー情報をPOSTした結果を取得する
				ServerApiUtils.requestPostServerApiJson(setting, setting.getWeb().getBookSiteListSaveInfoUrl(),
						FileUtils.readFileToString(localDataFile, "UTF-8"));
			}
		}, 3 * 60 * 1000);

		return true;
	}

	/**
	 * 処理対象サイト情報を更新(主にアフィリエイトURL用)
	 *
	 * @param siteInfoBean
	 * @return
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	protected boolean saveSiteInfo(SiteInfoBean siteInfoBean)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		// サーバー情報をPOSTした結果を取得する
		return ServerApiUtils.requestPostServerApi(setting,
				StringUtils.replace(setting.getWeb().getSiteInfoUrl(), "{siteName}", siteInfoBean.getSite_name()),
				ServerApiUtils.createRequestParams(siteInfoBean));
	}

	/**
	 * 指定された書籍詳細情報をサーバーに送信して更新/登録する。
	 *
	 * @param bookInfo
	 * @return
	 */
	protected boolean saveBookInfo(BookInfoBean bookInfo) {

		// リクエストパラメーターを生成する
		List<NameValuePair> requestParams = new ArrayList<>();
		requestParams.add(new BasicNameValuePair("site_id", Integer.toString(bookSiteInfo.getSite().getSite_id())));

		// 書籍情報のマップ
		Map<String, String> bookInfoMap;
		try {
			bookInfoMap = BeanUtils.describe(bookInfo);
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			throw new HibicomiFailureException("saveBookInfo リクエストパラメーター  BeanUtils.describe", e);
		}

		// 普通のプロパティ
		for (String name : new String[] { "book_id", "title", "series_title", "title_number", "complete_flg", "author",
				"catchcopy", "book_site_id", "book_site_url", "book_site_image", "book_site_title" }) {
			// logger.debug(name + ":"+ bookInfoMap.get(name));
			if (!StringUtils.isEmpty(bookInfoMap.get(name))) {
				requestParams.add(new BasicNameValuePair(name, bookInfoMap.get(name)));
			}
		}

		// LocalDateTime
		for (String name : new String[] { "publish_start_datetime", "publish_end_datetime", "free_start_datetime",
				"free_end_datetime", "scraped_datetime" }) {
			if (!StringUtils.isEmpty(bookInfoMap.get(name))) {
				LocalDateTime ldt;
				try {
					ldt = (LocalDateTime) PropertyUtils.getProperty(bookInfo, name);
				} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
					throw new HibicomiFailureException("saveBookInfo リクエストパラメーター LocalDateTime失敗", e);
				}
				requestParams.add(new BasicNameValuePair(name, ldt.format(MyFileUtils.JSON_DATETIME_FORMATTER)));
			}
		}

		// 文字列リスト
		for (String name : new String[] { "tag_list", "genre_list" }) {
			if (!StringUtils.isEmpty(bookInfoMap.get(name))) {
				List<String> list;
				try {
					list = (List<String>) PropertyUtils.getProperty(bookInfo, name);
					// logger.debug(bookInfo.getTitle() +":: list: name="+ name + ", size: "+
					// list.size());
					if (list.size() > 0) {
						requestParams.add(new BasicNameValuePair(name, StringUtils.join(list, ",")));
					}
				} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
					throw new HibicomiFailureException("saveBookInfo リクエストパラメーター 文字列リスト失敗", e);
				}
			}
		}

		// サイト情報
		requestParams.add(new BasicNameValuePair("site_id", Integer.toString(bookSiteInfo.getSite().getSite_id())));

		// post用URL
		String url;
		try {
			url = StringUtils.replace(setting.getWeb().getBookSiteInfoUrl(), "{bookSiteUrl}",
					codec.encode(bookInfo.getBook_site_url()));
		} catch (EncoderException e) {
			throw new HibicomiFailureException("saveBookInfo URLエンコード失敗", e);
		}

		// 書籍詳細URLをキーとして、サーバーに該当情報を送信する
		// サーバー情報をPOSTした結果を取得する
		return ServerApiUtils.requestPostServerApi(setting, url, requestParams);
	}

}
