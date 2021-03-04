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

public class MangaOkokuDetailScraper extends BaseDetailScraper {

	public MangaOkokuDetailScraper(WebDriver driver, JavascriptExecutor js, SettingBean setting, SiteBean site) {
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
		bookInfo.setBook_site_title(driver.findElement(By.xpath("//h1[@class='book-info--title']/span[@itemprop='name']")).getText());
		// フォーマット書名
		bookInfo.setTitle(BookTextUtils.formatTitle(bookInfo.getBook_site_title()));
		// 巻数字
		bookInfo.setTitle_number(BookTextUtils.formatTitleNumber(bookInfo.getBook_site_title()));
		// 著者
		bookInfo.setAuthor(StringUtils.join(driver.findElements(By.xpath("//dt[@class='book-info--detail-title'][text() = '著者・作者']/following-sibling::dd/a"))
				.stream()
				.filter(ae -> StringUtils.contains(ae.getAttribute("href"), "/author/"))
				.map(ae -> ae.getText()).collect(Collectors.toList()), ", "));
		// 概要
		bookInfo.setDescription(BookTextUtils.formatDescription(driver.findElement(By.className("book-info--desc-text")).getText()));
		// 表紙画像
		bookInfo.setBook_site_image(driver.findElement(By.className("book-info--img")).getAttribute("src"));
		// ジャンル
		for ( WebElement ae : driver.findElements(By.xpath("//dt[@class='book-info--detail-title'][text() = 'ジャンル']/following-sibling::dd/a")) ) {
			if (StringUtils.contains(ae.getAttribute("href"), "/category/")) {
				bookInfo.addGenre_list( BookTextUtils.formatGenre(ae.getText(), setting.getIndividualSetting().getScrapingSetting().getGenreMap()));
			}
		}
		// タグ
		for ( WebElement ae : driver.findElements(By.xpath("//dt[@class='book-info--detail-title'][text() = 'キーワード']/following-sibling::dd/a")) ) {
			bookInfo.addTag_list(ae.getText());
		}
		// キャッチコピー
		bookInfo.setCatchcopy(SudachiUtils.getInstance(setting).createCatchcopy(bookInfo.getDescription()));
		// 概要はクリア
		bookInfo.setDescription(null);

		logger.debug("■■■ "+ bookInfo.toString());


		// 無料期間は一覧側で取得済みなのでここでは常にtrue

		return true;
	}




}
