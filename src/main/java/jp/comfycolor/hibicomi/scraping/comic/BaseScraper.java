package jp.comfycolor.hibicomi.scraping.comic;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import jp.comfycolor.hibicomi.bean.info.BookInfoBean;
import jp.comfycolor.hibicomi.bean.info.BookSiteInfoBean;
import jp.comfycolor.hibicomi.bean.info.SiteInfoBean;
import jp.comfycolor.hibicomi.bean.setting.ScrapingSettingBean.PageBean;
import jp.comfycolor.hibicomi.bean.setting.ScrapingSettingBean.SiteBean;
import jp.comfycolor.hibicomi.bean.setting.SettingBean;
import jp.comfycolor.hibicomi.scraping.comic.detail.BaseDetailScraper;
import jp.comfycolor.hibicomi.utils.api.ServerApiUtils;
import jp.comfycolor.hibicomi.utils.file.MyFileUtils;
import jp.comfycolor.hibicomi.utils.retry.Proc;
import jp.comfycolor.hibicomi.utils.retry.Runnable;
import jp.comfycolor.hibicomi.utils.scraping.ScrapeUtils;

public abstract class BaseScraper implements Callable<Boolean> {

	public static Logger logger = LoggerFactory.getLogger(BaseScraper.class);

	protected BaseDetailScraper detailScraper;

	protected SettingBean setting;
	protected SiteBean site;
	protected WebDriver driver;
	protected JavascriptExecutor js;
	protected BookSiteInfoBean bookSiteInfo;

	protected URLCodec codec;

	public BaseScraper(SettingBean setting, SiteBean site) {
		this.setting = setting;
		this.site = site;

		codec = new URLCodec("UTF-8");

		this.bookSiteInfo = new BookSiteInfoBean();
		SiteInfoBean serverSite = requestSiteInfo();
		this.bookSiteInfo.getSite().setSite_id(serverSite.getSite_id());
		this.bookSiteInfo.getSite().setSite_name(serverSite.getSite_name());
		// アフィリエイトURLは設定ファイルのを優先させる
		this.bookSiteInfo.getSite().setSite_affiliate_url(site.getAffiliateUrl());

		// Chrome(非表示) でWebドライバ生成
		System.setProperty("webdriver.chrome.driver",
				setting.getIndividualSetting().getScrapingSetting().getDriverPath().getAbsolutePath());
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--headless");
		options.addArguments("--window-size=1280,720");
		this.driver = new ChromeDriver(options);
		this.js = (JavascriptExecutor) driver;
	}

	@Override
	public Boolean call() throws Exception {
		boolean result = execute();

		// ドライバ終了
		driver.quit();

		return result;
	}

	/**
	 * 実行メソッド
	 *
	 * @param setting
	 * @throws Exception
	 */
	public boolean execute() throws Exception {

		for (PageBean page : site.getPageList()) {
			for ( String url : page.getUrlList() ) {
				Proc.retry(3, new Runnable() {
					@Override
					public void run() throws Throwable {
						logger.info("■ "+ site.getName() + ": "+ page.getName() +", "+ url);
						scrapeUrl(page, url);
					}
				}, 3 * 60 * 1000);
			}
		}

		// 結果をJSON出力
		outputJson();

		return true;
	}

	protected boolean scrapeDetail(BookInfoBean bookInfo) throws Exception {
		// 一旦待機
		ScrapeUtils.sleep(setting);

		logger.debug("scrapeDetail detailScraper.scrapeUrl: "+ bookInfo.getBook_site_url());

		// 詳細情報取得
		return detailScraper.scrapeUrl(bookInfo);
	}

	/**
	 * 収集上限を超えたならtrue
	 * @return
	 */
	protected boolean isLimitOver(PageBean page) {
		return isLimitOver(page, new ArrayList<>());
	}

	/**
	 * 収集上限を超えたならtrue
	 * @return
	 */
	protected boolean isLimitOver(PageBean page, List<BookInfoBean> currentBookInfoList) {
		// 上限にマイナス値が設定されていたら上限無制限
		if (setting.getIndividualSetting().getScrapingSetting().getLimit() < 0) {
			return false;
		}
		// それ以外は、上限数チェックして返す
		return bookSiteInfo.getBook_list(site, page).size() + currentBookInfoList.size()
					>= setting.getIndividualSetting().getScrapingSetting().getLimit();
	}

	/**
	 * JSON保存
	 * @throws IOException
	 */
	protected void outputJson() throws IOException {

		// ディレクトリ初期化
		File dir = MyFileUtils.initJsonDir(setting);
		String fileName = site.getName()
				+ "_" +  MyFileUtils.NOW_DIR_FORMATTER.format(setting.getNow())
				+ ".json";

		logger.info("JSON保存開始: "+ dir.getAbsolutePath() + " / "+ fileName);

		// JSON出力
		Gson gson = MyFileUtils.createBookInfoGson();

		// HTML文字列出力
		FileUtils.writeStringToFile(new File(dir, fileName), gson.toJson(bookSiteInfo), "UTF-8");

		logger.info("JSON保存終了");
	}

	/**
	 * 処理対象サイト情報を取得
	 *
	 * @return
	 */
	protected SiteInfoBean requestSiteInfo() {
		String response = ServerApiUtils.requestGetServerApi(setting
				, StringUtils.replace(setting.getWeb().getSiteInfoUrl(), "{siteName}", site.getName()));

		Gson gson = new Gson();
		SiteInfoBean serverSite = gson.fromJson(response, SiteInfoBean.class);

		logger.debug("serverSite: "+ serverSite.toString());

		return serverSite;
	}

	/**
	 * 詳細を取得するかチェックする
	 *
	 * @param currentBookInfoList
	 * @return
	 */
	protected List<BookInfoBean> requestDetailTargetBookInfo(List<BookInfoBean> currentBookInfoList) {
		List<BookInfoBean> targetBookInfoList = new ArrayList<>();

		// 処理対象URLを配列形式で詰める
		List<NameValuePair> requestParams = new ArrayList<>();
		for (BookInfoBean bookInfo : currentBookInfoList) {
			requestParams.add(new BasicNameValuePair("urls[]", bookInfo.getBook_site_url()));
		}

		// 結果を文字列で取得する
		String responseData =
				ServerApiUtils.requestPostServerApiString(setting, setting.getWeb().getBookSiteListExistedInfoUrl(), requestParams);
		logger.debug(responseData);

		// Beanに変換
		Gson gson = MyFileUtils.createBookInfoGson();
		List<BookInfoBean> existedBookList = gson.fromJson(responseData, new TypeToken<List<BookInfoBean>>(){}.getType());

		for (int i = 0; i < existedBookList.size(); i++) {
			BookInfoBean existedBookInfo = existedBookList.get(i);
			BookInfoBean currentBookInfo = currentBookInfoList.get(i);

			logger.debug(existedBookInfo.toString());

			if (existedBookInfo.getBook_id() <= 0) {
				// 書籍IDが入っていない場合、レコードなし
				logger.debug("<<×>> 書籍情報なし: "+ existedBookInfo.getBook_site_url());
				targetBookInfoList.add(currentBookInfo);
			}
			else {
				// 書籍IDが入っていれば、とりあえずレコード存在
				if (setting.getIndividualSetting().getScrapingSetting().isExistedSkip()
						|| setting.getIndividualSetting().getScrapingSetting().isExistedStop()) {

					if ( setting.getIndividualSetting().getScrapingSetting().isCheckFreeEnd() &&
							existedBookInfo.getFree_end_datetime() == null && currentBookInfo.getFree_end_datetime() == null) {
						logger.debug("<<○>> 書籍情報あり 無料期間NULL: "+ existedBookInfo.toString());
						// 無料期間が両方なしで同じなら既存とみなす

						if (setting.getIndividualSetting().getScrapingSetting().isExistedStop()) {
							// 既存終了ならば、ループ終了
							break;
						}
					}
					else if ( setting.getIndividualSetting().getScrapingSetting().isCheckFreeEnd() &&
							currentBookInfo.getFree_end_datetime() != null &&
							existedBookInfo.getFree_end_datetime().withNano(0).isEqual(currentBookInfo.getFree_end_datetime().withNano(0))) {
						logger.debug("<<○>> 書籍情報あり 無料期間イコール: "+ existedBookInfo.toString());
						// 無料期間が同じなら既存とみなす

						if (setting.getIndividualSetting().getScrapingSetting().isExistedStop()) {
							// 既存終了ならば、ループ終了
							break;
						}
					}
					else {
						if (setting.getIndividualSetting().getScrapingSetting().isCheckFreeEnd()) {
							// 無料期間が違う場合、処理対象に含める
							logger.debug("<<×>> 書籍情報あり　無料期間違い: "+ existedBookInfo.toString());
							targetBookInfoList.add(currentBookInfo);
						}
						else {
							logger.debug("<<○>> 書籍情報あり　無料期間スルー: "+ existedBookInfo.toString());
						}
					}
				}
			}
		}

		return targetBookInfoList;
	}


	public abstract boolean scrapeUrl(PageBean page, String url) throws Exception;

}
