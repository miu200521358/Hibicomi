package jp.comfycolor.hibicomi.utils.scraping;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import jp.comfycolor.hibicomi.bean.info.BookInfoBean;
import jp.comfycolor.hibicomi.bean.setting.SettingBean;
import jp.comfycolor.hibicomi.utils.api.ServerApiUtils;
import jp.comfycolor.hibicomi.utils.err.HibicomiFailureException;
import jp.comfycolor.hibicomi.utils.file.MyFileUtils;

public class ScrapeUtils {

	public static Logger logger = LoggerFactory.getLogger(ScrapeUtils.class);

	protected static URLCodec codec = new URLCodec("UTF-8");;

	/**
	 * 待機
	 */
	public static void sleep(SettingBean setting) {
		sleep(setting.getIndividualSetting().getScrapingSetting().getDetailPauseSecond());
	}

	public static void sleep(int second) {
		try {
			Thread.sleep(second * 1000);
		} catch (InterruptedException e) {
			logger.error("スリープ失敗", e);
		}
	}

	/**
	 * エレメントが押下可能になるまでスクロールしてクリックする
	 */
	public static void scrollAndClick(WebDriver driver, JavascriptExecutor js, WebElement element) {
		int elementPosition = element.getLocation().getY() - 20;

		logger.debug("elementPosition: "+ elementPosition);

		js.executeScript(String.format("window.scroll(0, %s)", elementPosition));
		ScrapeUtils.waitReadyStateComplete(driver, js);
		element.click();
	}

	/**
	 * ページ読み込み完了まで待機して取得する
	 * @param js TODO
	 */
	public static void waitReadyStateComplete(WebDriver driver, JavascriptExecutor js) {
		// 固定で待機
		sleep(2);
		// ページ読み込み完了まで待機
		WebDriverWait wait = new WebDriverWait(driver, 30);
		wait.until(new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver driver) {
//				logger.debug("ExpectedCondition");

				// HTMLの読み込み完了
				String readyState = js.executeScript("return document.readyState")
						.toString();
//				logger.debug("readyState: " + readyState);

				if ( readyState.equals("complete") ) {

					// jQueryの定義完了
					boolean jQueryDefine = (Boolean) js.executeScript("return window.jQuery != undefined");
//					logger.debug("jQueryDefine: " + jQueryDefine);

					if (jQueryDefine) {
						// jQueryの読み込み完了
						String jQueryState = js.executeScript("return jQuery.active").toString();
//						logger.debug("jQueryState: " + jQueryState);

						return jQueryState.equals("0");
					}

					return false;
				}
				else {
					return false;
				}
			}
		});
	}

	/**
	 * 指定された書籍詳細情報がサーバーに存在するか返す
	 *
	 * @param bookInfo
	 * @return
	 */
	public static boolean existBookSiteInfoByFreeEnd(SettingBean setting, BookInfoBean bookInfo) {
		return existBookSiteInfoByFreeEnd(setting, bookInfo, bookInfo.getBook_site_url());
	}

	/**
	 * 指定された書籍詳細情報がサーバーに存在するか返す
	 *
	 * @param bookInfo
	 * @return
	 */
	public static boolean existBookSiteInfoByFreeEnd(SettingBean setting, BookInfoBean bookInfo, String detailUrl) {

		// 書籍情報を取得する
		BookInfoBean existedbookInfo = getExistedBookSiteInfo(setting, bookInfo, detailUrl);

		if (existedbookInfo == null) {
			// サイト別書籍情報がなければfalse
			return false;
		}

		// 書籍IDを設定する
		bookInfo.setBook_id(existedbookInfo.getBook_id());

		// サイト別書籍IDを設定する
		bookInfo.setBook_site_id(existedbookInfo.getBook_site_id());

		logger.debug( existedbookInfo.getBook_id() +" existed="+ existedbookInfo.getFree_end_datetime() +", target="+ bookInfo.getFree_end_datetime());

		if (existedbookInfo.getFree_end_datetime() == null && bookInfo.getFree_end_datetime() == null) {
			logger.debug("<<○>>existBookSiteInfo 書籍情報あり 無料期間NULL: "+ existedbookInfo.toString());
			// 無料期間が両方なしで同じなら既存とみなす
			return true;
		}

		if (bookInfo.getFree_end_datetime() != null &&
				existedbookInfo.getFree_end_datetime().withNano(0).isEqual(bookInfo.getFree_end_datetime().withNano(0))) {
			logger.debug("<<○>>existBookSiteInfo 書籍情報あり 無料期間イコール: "+ existedbookInfo.toString());
			// 無料期間が同じなら既存とみなす
			return true;
		}

		logger.debug("<<×>>existBookSiteInfo 書籍情報あり　無料期間違い: "+ existedbookInfo.toString());

		// レコードが存在しているが、無料期間が違っている場合、false
		return false;
	}

	/**
	 * 指定された書籍詳細情報がサーバーに存在するか返す
	 *
	 * @param bookInfo
	 * @return
	 */
	public static boolean existBookSiteInfoByUrl(SettingBean setting, BookInfoBean bookInfo) {
		return existBookSiteInfoByUrl(setting, bookInfo, bookInfo.getBook_site_url());
	}

	/**
	 * 指定された書籍詳細情報がサーバーに存在するか返す
	 *
	 * @param bookInfo
	 * @return
	 */
	public static boolean existBookSiteInfoByUrl(SettingBean setting, BookInfoBean bookInfo, String detailUrl) {

		// 書籍情報を取得する
		BookInfoBean existedbookInfo = getExistedBookSiteInfo(setting, bookInfo, detailUrl);

		if (existedbookInfo == null) {
			// サイト別書籍情報がなければfalse
			return false;
		}

		// 書籍IDを設定する
		bookInfo.setBook_id(existedbookInfo.getBook_id());

		// サイト別書籍IDを設定する
		bookInfo.setBook_site_id(existedbookInfo.getBook_site_id());

		logger.debug( existedbookInfo.getBook_id() +" existed="+ existedbookInfo.getFree_end_datetime() +", target="+ bookInfo.getFree_end_datetime());
		logger.debug("<<○>>existBookSiteInfo 書籍情報あり: "+ existedbookInfo.toString());

		// URLでレコードがヒットしたら常にTRUE
		return true;
	}

	/**
	 * サーバーのサイト別書籍情報を取得する
	 *
	 * @param setting
	 * @param bookInfo
	 * @param detailUrl
	 * @return
	 */
	public static BookInfoBean getExistedBookSiteInfo(SettingBean setting, BookInfoBean bookInfo, String detailUrl) {
		// get用URL
		String url;
		try {
			url = StringUtils.replace(setting.getWeb().getBookSiteInfoUrl(), "{bookSiteUrl}",
					codec.encode(detailUrl));
		} catch (EncoderException e) {
			throw new HibicomiFailureException("existBookSiteInfo URLエンコード失敗", e);
		}

		// 書籍詳細URLを元にサーバーから該当情報をGETする
		String response = ServerApiUtils.requestGetServerApi(setting, url);

		if (StringUtils.isEmpty( StringUtils.trimToEmpty(response) )) {
			logger.debug("<<×>>existBookSiteInfo 書籍情報なし: "+ detailUrl);
			// 書籍情報がなかった場合、null
			return null;
		}

		Gson gson = MyFileUtils.createBookInfoGson();
		return gson.fromJson(response, BookInfoBean.class);
	}

}
