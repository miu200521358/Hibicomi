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

public class RentaDetailScraper extends BaseDetailScraper {

	public RentaDetailScraper(WebDriver driver, JavascriptExecutor js, SettingBean setting, SiteBean site) {
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
		List<WebElement> continueEs = driver.findElement(By.id("desc_text")).findElements(By.xpath("//a[text()='もっと読む']"));
		if (continueEs.size() > 0 && continueEs.get(0).isDisplayed()) {
			continueEs.get(0).click();
			ScrapeUtils.waitReadyStateComplete(driver, js);
		}

		// オリジナル書名
		bookInfo.setBook_site_title(driver.findElement(By.tagName("h1")).getText());
		// フォーマット書名
		bookInfo.setTitle(BookTextUtils.formatTitle(bookInfo.getBook_site_title()));
		// 巻数字
		bookInfo.setTitle_number(BookTextUtils.formatTitleNumber(bookInfo.getBook_site_title()));
		// 完結フラグ
		bookInfo.setComplete_flg(driver.findElements(By.xpath("//b[text() = '完結']")).size() > 0);
		// 著者
		bookInfo.setAuthor(StringUtils.join(driver.findElements(By.xpath("//span[@data-book='author']/a"))
				.stream()
				.map(ae -> ae.getText()).collect(Collectors.toList()), ", "));
		// 概要
		bookInfo.setDescription( BookTextUtils.formatDescription(driver.findElement(By.id("desc_text")).getText()));
		// ジャンル
		for ( WebElement ae : driver.findElements(By.xpath("//td[text() = 'ジャンル']/following-sibling::td/a")) ) {
			// 最初の一件だけジャンル。後はタグ扱い。
			if (bookInfo.getGenre_list().size() == 0) {
				bookInfo.addGenre_list( BookTextUtils.formatGenre(ae.getText(), setting.getIndividualSetting().getScrapingSetting().getGenreMap()));
			}
			else {
				bookInfo.addTag_list(ae.getText());
			}
		}
		// タグ
		for ( WebElement ae : driver.findElements(By.xpath("//td[text() = 'タグ']/following-sibling::td/a")) ) {
			if (!StringUtils.contains(ae.getText(), "今だけお得!")
					&& !StringUtils.contains(ae.getText(), "だれでも無料")
					&& !StringUtils.contains(ae.getText(), "会員限定無料")) {
				bookInfo.addTag_list(ae.getText());
			}
		}
		// 表紙画像
		bookInfo.setBook_site_image(driver.findElement(By.className("cover_wrap")).findElement(By.tagName("img")).getAttribute("src"));
		// キャッチコピー
		bookInfo.setCatchcopy(SudachiUtils.getInstance(setting).createCatchcopy(bookInfo.getDescription()));
		// 概要はクリア
		bookInfo.setDescription(null);

		logger.debug("■■■ "+ bookInfo.toString());


		// 無料期間は一覧側で取得済みなのでここでは常にtrue

		return true;
	}




}
