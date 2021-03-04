package jp.comfycolor.hibicomi.scraping.comic.detail;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

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

public class HandyDetailScraper extends BaseDetailScraper {

	public HandyDetailScraper(WebDriver driver, JavascriptExecutor js, SettingBean setting, SiteBean site) {
		super(driver, js, setting, site);
	}

	@Override
	public boolean scrapeUrl(BookInfoBean bookInfo) throws Exception {
		// URL読み込み
		driver.get(bookInfo.getBook_site_url());
		ScrapeUtils.waitReadyStateComplete(driver, js);

		// 内容紹介をもっと読む押下
		List<WebElement> continueEs = driver.findElement(By.className("read_more_js")).findElements(By.xpath("//a[text()='続きを読む']"));
		if (continueEs.size() > 0 && continueEs.get(0).isDisplayed()) {
			continueEs.get(0).click();
			ScrapeUtils.waitReadyStateComplete(driver, js);
		}

		// 詳細URL
		bookInfo.setBook_site_url(driver.getCurrentUrl());

//		// アフィリエイトリンク作成
//		if (StringUtils.isNotEmpty(site.getAffiliateUrl())) {
//			bookInfo.setBook_site_affiliate_url(
//					StringUtils.replace(site.getAffiliateUrl(), "{originalUrl}",
//							codec.encode(driver.getCurrentUrl())));
//		}

		// オリジナル書名
		bookInfo.setBook_site_title(driver.findElement(By.className("product-title__txt")).getText());
		// フォーマット書名
		bookInfo.setTitle(BookTextUtils.formatTitle(bookInfo.getBook_site_title()));
		// 巻数字
		bookInfo.setTitle_number(BookTextUtils.formatTitleNumber(bookInfo.getBook_site_title()));
		// 著者
		bookInfo.setAuthor(StringUtils.join(driver.findElements(By.xpath("//div[@class='author-list']/a"))
				.stream()
				.map(ae -> ae.getText()).collect(Collectors.toList()), ", "));
		// 概要
		bookInfo.setDescription( BookTextUtils.formatDescription(driver.findElement(By.className("more_abst")).getText()));
		// ジャンル
		for ( WebElement ae : driver.findElements(By.xpath("//span[@class = 'category-list__link-array']/a")) ) {
			bookInfo.addGenre_list( BookTextUtils.formatGenre(ae.getText(), setting.getIndividualSetting().getScrapingSetting().getGenreMap()));
		}
		// タグ
		for ( WebElement ae : driver.findElements(By.xpath("//p[text() = 'ジャンル']/following-sibling::ul[@class='tag-area']/li[@class='tag__item']/a")) ) {
			bookInfo.addTag_list(ae.getText());
		}
		// 表紙画像
		bookInfo.setBook_site_image(driver.findElement(By.className("main-book__thumb")).getAttribute("src"));

		// 無料期間
		if (driver.findElements(By.xpath("//div[@class='description']")).size() > 0) {
			for (WebElement came : driver.findElements(By.xpath("//div[@class='description']/p")) ) {
				if (StringUtils.contains(came.getText(), "販売終了日")) {

					String freeEnd = StringUtils.trim(StringUtils.remove(came.getText(), "販売終了後は閲覧できません。"));
					if (StringUtils.isNotEmpty(freeEnd)) {
						LocalDate ld = LocalDate.parse(freeEnd, DateTimeFormatter.ofPattern("販売終了日：yyyy.M.d"));

						// 月が現在より前の場合、来年と見なす
						int yearOffset = (ld.getMonthValue() < LocalDate.now().getMonthValue()) ? 1 : 0;
						bookInfo.setFree_end_datetime(ld.plusYears(yearOffset).atTime(LocalTime.MAX));
					}
				}
			}
		}
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
