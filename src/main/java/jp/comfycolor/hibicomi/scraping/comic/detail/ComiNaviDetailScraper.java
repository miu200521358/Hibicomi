package jp.comfycolor.hibicomi.scraping.comic.detail;

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

public class ComiNaviDetailScraper extends BaseDetailScraper {

	public ComiNaviDetailScraper(WebDriver driver, JavascriptExecutor js, SettingBean setting, SiteBean site) {
		super(driver, js, setting, site);
	}

	@Override
	public boolean scrapeUrl(BookInfoBean bookInfo) throws Exception {
		// URL読み込み
		driver.get(bookInfo.getBook_site_url());
		ScrapeUtils.waitReadyStateComplete(driver, js);

//		// 内容紹介をもっと読むボタンまでスクロール
//		js.executeScript("document.getElementById('button_open').scrollIntoView(true);");
//		ScrapeUtils.waitReadyStateComplete(driver);

		// 内容紹介をもっと読む押下
		List<WebElement> continueEs = driver.findElements(By.xpath("//input[@id='button_open'][@value='　紹介文を全部見る▼']"));
//		logger.debug("continueEs size: "+ continueEs.size() + ", display="+ continueEs.get(0).isDisplayed());
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
		bookInfo.setBook_site_title(driver.findElement(By.className("content-title-name")).getText());
		// フォーマット書名
		bookInfo.setTitle(BookTextUtils.formatTitle(bookInfo.getBook_site_title()));
		// 巻数字
		bookInfo.setTitle_number(BookTextUtils.formatTitleNumber(bookInfo.getBook_site_title()));
		// 著者
		bookInfo.setAuthor(StringUtils.replace(StringUtils.join(driver.findElements(By.xpath("//span[@class='bookTitle']/span[@class='content-author-name']/a"))
				.stream()
				.map(ae -> ae.getText()).collect(Collectors.toList()), ", "), "･", ", "));
		// 概要
		String description = driver.findElement(By.className("content-title-info")).getText();
//		logger.debug("detail size: "+ driver.findElements(By.className("detail")).size());
		if (driver.findElements(By.className("detail")).size() > 0) {
//			logger.debug("detail: "+ driver.findElement(By.className("detail")));
//			logger.debug("detail text: "+ driver.findElement(By.className("detail")).getText());
			description += driver.findElement(By.className("detail")).getText();
		}
		bookInfo.setDescription( BookTextUtils.formatDescription(description));
//		logger.debug("description: "+ bookInfo.getDescription());
		for ( WebElement ae : driver.findElements(By.xpath("//span[@class = 'content-title-genre-list']/a")) ) {
			// ジャンル
			if (bookInfo.getGenre_list().size() == 0) {
				bookInfo.addGenre_list( BookTextUtils.formatGenre(ae.getText(), setting.getIndividualSetting().getScrapingSetting().getGenreMap()));
			}
			// タグ
			else {
				if (!StringUtils.contains(ae.getText(), "雑誌")) {
					bookInfo.addTag_list(ae.getText());
				}
			}
		}
		// 表紙画像
		bookInfo.setBook_site_image(driver.findElement(By.className("content-title-image")).findElement(By.tagName("img")).getAttribute("src"));
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
