package jp.comfycolor.hibicomi.scraping.comic.detail;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import jp.comfycolor.hibicomi.bean.info.BookInfoBean;
import jp.comfycolor.hibicomi.bean.setting.ScrapingSettingBean.SiteBean;
import jp.comfycolor.hibicomi.bean.setting.SettingBean;
import jp.comfycolor.hibicomi.utils.scraping.ScrapeUtils;
import jp.comfycolor.hibicomi.utils.text.BookTextUtils;
import jp.comfycolor.hibicomi.utils.text.SudachiUtils;

public class CmoaDetailScraper extends BaseDetailScraper {

	public CmoaDetailScraper(WebDriver driver, JavascriptExecutor js, SettingBean setting, SiteBean site) {
		super(driver, js, setting, site);
	}

	/* (非 Javadoc)
	 * @see jp.comfycolor.hibicomi.scraping.comic.detail.BaseDetailScraper#scrapeUrl(jp.comfycolor.hibicomi.bean.setting.ScrapingSettingBean.PageBean, java.lang.String, jp.comfycolor.hibicomi.bean.setting.BookSiteInfoBean.BookInfoBean)
	 */
	@Override
	public boolean scrapeUrl(BookInfoBean bookInfo) throws Exception {
		// URL読み込み
		driver.get(bookInfo.getBook_site_url());
		ScrapeUtils.waitReadyStateComplete(driver, js);

		// 詳細URL
		bookInfo.setBook_site_url(driver.getCurrentUrl());

//		// アフィリエイトリンク作成
//		if (StringUtils.isNotEmpty(site.getAffiliateUrl())) {
//			bookInfo.setBook_site_affiliate_url(
//					StringUtils.replace(site.getAffiliateUrl(), "{originalUrl}",
//							codec.encode(driver.getCurrentUrl())));
//		}

		// 内容紹介をもっと読む押下
		List<WebElement> continueEs = driver.findElement(By.id("description_btn")).findElements(By.xpath("//a[text()='【続きを読む】']"));
		if (continueEs.size() > 0 && continueEs.get(0).isDisplayed() ) {
			continueEs.get(0).click();
			ScrapeUtils.waitReadyStateComplete(driver, js);
		}

		// オリジナル書名
		bookInfo.setBook_site_title(driver.findElement(By.className("title_details_title_name")).getText());
		// フォーマット書名
		bookInfo.setTitle(BookTextUtils.formatTitle(bookInfo.getBook_site_title()));
		// 巻数字
		bookInfo.setTitle_number(BookTextUtils.formatTitleNumber(bookInfo.getBook_site_title()));
		// 完結フラグ
		bookInfo.setComplete_flg(driver.findElements(By.xpath("//span[@class='end_m']")).size() > 0);
		// 著者
		bookInfo.setAuthor(StringUtils.join(driver.findElements(By.xpath("//div[@class='title_details_author_name']/a"))
				.stream()
				.map(ae -> ae.getText()).collect(Collectors.toList()), ", "));
		// 概要
		bookInfo.setDescription( BookTextUtils.formatDescription(driver.findElement(By.id("comic_description")).getText()));
		// 無料期間
		if (driver.findElements(By.className("cam_until")).size() > 0) {
			for (WebElement came : driver.findElements(By.className("cam_until")) ) {
				if (StringUtils.contains(came.getText(), "【期間限定無料あり】")) {

					String freeEnd = came.getText();
					if (StringUtils.isNotEmpty(freeEnd)) {
						LocalDateTime ldt = LocalDateTime.parse(freeEnd, DateTimeFormatter.ofPattern("【期間限定無料あり】yyyy/M/d H:mmまで"));

						// 月が現在より前の場合、来年と見なす
						int yearOffset = (ldt.getMonthValue() < LocalDate.now().getMonthValue()) ? 1 : 0;
						bookInfo.setFree_end_datetime(ldt.plusYears(yearOffset).minusSeconds(1));
					}
				}
			}
		}

		// ジャンル
		for ( WebElement ae : driver.findElements(By.xpath("//div[@class='category_line_f_l_l'][text() = 'ジャンル']/following-sibling::div/a")) ) {
			bookInfo.addGenre_list( BookTextUtils.formatGenre(ae.getText(), setting.getIndividualSetting().getScrapingSetting().getGenreMap()));
		}
		// タグ
		for ( WebElement ae : driver.findElements(By.xpath("//div[@class='category_line_f_r_l genre_detail']/a")) ) {
			if (StringUtils.contains(ae.getAttribute("href"), "/genre/")) {
				// BLとかTLは除去する
				bookInfo.addTag_list(RegExUtils.removeAll(ae.getText(), Pattern.compile("\\([^\\)]+\\)")));
			}
		}
		// シリーズ
		for ( WebElement ae : driver.findElements(By.xpath("//div[@class='category_line_f_r_l']/a")) ) {
			if (StringUtils.contains(ae.getAttribute("href"), "/series/")) {
				bookInfo.setSeries_title(ae.getText());
			}
		}

		// 表紙画像
		bookInfo.setBook_site_image(driver.findElement(By.className("title_big_thum")).getAttribute("src"));
		// キャッチコピー
		bookInfo.setCatchcopy(SudachiUtils.getInstance(setting).createCatchcopy(bookInfo.getDescription()));

		// 概要はクリア
		bookInfo.setDescription(null);

		logger.debug("■■■ "+ bookInfo.toString());

		// 既存ならば停止設定で、かつサイトに既存の場合、false
		if (setting.getIndividualSetting().getScrapingSetting().isExistedStop()
				&& ScrapeUtils.existBookSiteInfoByFreeEnd(setting, bookInfo)) {
			return false;
		}

		return true;
	}




}
