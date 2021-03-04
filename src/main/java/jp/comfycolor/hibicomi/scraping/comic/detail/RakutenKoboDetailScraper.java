package jp.comfycolor.hibicomi.scraping.comic.detail;

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

public class RakutenKoboDetailScraper extends BaseDetailScraper {

	public RakutenKoboDetailScraper(WebDriver driver, JavascriptExecutor js, SettingBean setting, SiteBean site) {
		super(driver, js, setting, site);
	}

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

		// オリジナル書名
		WebElement titleE = driver.findElement(By.id("productTitle"));
		String title = titleE.getText();
		// 著者名は書名から除去
		for ( WebElement authorE : titleE.findElements(By.className("authorLink")) ) {
			title = StringUtils.remove(title, authorE.getText());
		}
		bookInfo.setBook_site_title(title);
		// フォーマット書名
		bookInfo.setTitle(BookTextUtils.formatTitle(bookInfo.getBook_site_title()));
		// 巻数字
		bookInfo.setTitle_number(BookTextUtils.formatTitleNumber(bookInfo.getBook_site_title()));
		// 著者
		bookInfo.setAuthor(StringUtils.join(driver.findElements(By.xpath("//span[@class='category'][text() = '著者']/following-sibling::span/a"))
				.stream()
				.map(ae -> ae.getText())
				.map(str -> StringUtils.remove(str, "　"))
				.collect(Collectors.toList()), ", "));
		// 概要
		bookInfo.setDescription(BookTextUtils.formatDescription(driver.findElement(By.xpath("//div[@id='editArea2']/div[@class='free']/p")).getText()));
		// 表紙画像
		bookInfo.setBook_site_image(driver.findElement(By.className("item-imgArea")).findElement(By.tagName("img")).getAttribute("src"));
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
