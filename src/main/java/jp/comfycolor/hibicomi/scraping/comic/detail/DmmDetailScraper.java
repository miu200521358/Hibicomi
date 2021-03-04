package jp.comfycolor.hibicomi.scraping.comic.detail;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
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

public class DmmDetailScraper extends BaseDetailScraper {

	public DmmDetailScraper(WebDriver driver, JavascriptExecutor js, SettingBean setting, SiteBean site) {
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
		bookInfo.setBook_site_title(driver.findElement(By.className("m-boxDetailProduct__info__ttl")).getText());
		// フォーマット書名
		bookInfo.setTitle(BookTextUtils.formatTitle(bookInfo.getBook_site_title()));
		// 巻数字
		bookInfo.setTitle_number(BookTextUtils.formatTitleNumber(bookInfo.getBook_site_title()));
		// 著者
		bookInfo.setAuthor(StringUtils.join(driver.findElements(By.xpath("//li[@class='m-boxDetailProductInfoMainList__description__list__item']/a"))
				.stream()
				.filter(ae -> StringUtils.contains(ae.getAttribute("href"), "/author/"))
				.map(ae -> ae.getText()).collect(Collectors.toList()), ", "));
		// 概要
		bookInfo.setDescription( BookTextUtils.formatDescription(driver.findElement(By.className("m-boxDetailProduct__info__story")).getText()));
		for ( WebElement ae : driver.findElements(By.xpath("//li[@class='m-boxDetailProductInfo__list__description__item']/a")) ) {
			// カテゴリをジャンルに設定
			if (StringUtils.contains(ae.getAttribute("href"), "article=category")) {
				bookInfo.addGenre_list( BookTextUtils.formatGenre(ae.getText(), setting.getIndividualSetting().getScrapingSetting().getGenreMap()));
			}
			// タグ
			else if (StringUtils.contains(ae.getAttribute("href"), "article=genre") && !StringUtils.contains(ae.getAttribute("href"), "article=genre&id=47")) {
				bookInfo.addTag_list(ae.getText());
			}
		}
		// 表紙画像
		bookInfo.setBook_site_image(driver.findElement(By.className("m-imgDetailProductPack")).getAttribute("src"));

		// 無料期間
		List<WebElement> camEs = driver.findElements(By.xpath("//span[@class='m-boxSubDetailPurchase__campaignPeriod__txt']"));
		if (camEs.size() > 0) {
			if (StringUtils.isNotEmpty(camEs.get(0).getText())) {
				MonthDay md = MonthDay.parse(camEs.get(0).getText(), DateTimeFormatter.ofPattern("M月d日(E) HH:mmまで", Locale.JAPAN));

				// 月が現在より前の場合、来年と見なす
				int yearOffset = (md.getMonthValue() < LocalDate.now().getMonthValue()) ? 1 : 0;
				bookInfo.setFree_end_datetime(md.atYear(LocalDate.now().getYear() + yearOffset).atTime(LocalTime.MAX));
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
