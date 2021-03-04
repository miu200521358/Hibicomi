package jp.comfycolor.hibicomi.scraping.comic.detail;

import java.util.stream.Collectors;

import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import jp.comfycolor.hibicomi.bean.info.BookInfoBean;
import jp.comfycolor.hibicomi.bean.setting.ScrapingSettingBean.SiteBean;
import jp.comfycolor.hibicomi.bean.setting.SettingBean;
import jp.comfycolor.hibicomi.utils.scraping.ScrapeUtils;
import jp.comfycolor.hibicomi.utils.text.BookTextUtils;
import jp.comfycolor.hibicomi.utils.text.SudachiUtils;

public class EBookJapanDetailScraper extends BaseDetailScraper {

	public EBookJapanDetailScraper(WebDriver driver, JavascriptExecutor js, SettingBean setting, SiteBean site) {
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
		bookInfo.setBook_site_title(driver.findElement(By.id("volumeTitle")).getText());
		// フォーマット書名
		bookInfo.setTitle(BookTextUtils.formatTitle(bookInfo.getBook_site_title()));
		// 巻数字
		bookInfo.setTitle_number(BookTextUtils.formatTitleNumber(bookInfo.getBook_site_title()));
		// 著者
		bookInfo.setAuthor(StringUtils.join(
			driver.findElements(By.xpath("//p[@class='bookAuthor']/a"))
				.stream()
				// リンク文字列抽出
				.map(ae -> ae.getText())
				// 著者：等の文言を空白に変換
				.map(str -> RegExUtils.replaceAll(str, "(^|　)[^：]+：", "$1"))
				// /を空白に変換
				.map(str -> RegExUtils.replaceAll(str, "\\/", "　"))
				// 全角空白で分割
				.map(str -> StringUtils.split(str, "　"))
				// 各分割配列をカンマで結合
				.map(strs -> StringUtils.join(strs, ", "))
				// さらにリスト化
				.collect(Collectors.toList())
			, ", "));

		// 概要
		bookInfo.setDescription( BookTextUtils.formatDescription(driver.findElement(By.className("bookSummaryContent")).getText()));
		// ジャンル
		String genreOriginal = StringUtils.removeStart(StringUtils.trim(driver.findElement(By.xpath("//p[@class='bookGenre']")).getText()), "ジャンル：");
		bookInfo.addGenre_list( BookTextUtils.formatGenre(genreOriginal, setting.getIndividualSetting().getScrapingSetting().getGenreMap()));

		// タグなし
		// 表紙画像
		bookInfo.setBook_site_image(driver.findElement(By.className("bookThumbArea")).findElement(By.xpath("figure/a/img")).getAttribute("src"));
		// キャッチコピー
		bookInfo.setCatchcopy(SudachiUtils.getInstance(setting).createCatchcopy(bookInfo.getDescription()));
		// 概要はクリア
		bookInfo.setDescription(null);

		logger.debug("■■■ "+ bookInfo.toString());

		// 無料期間は一覧側で取得済みなのでここでは常にtrue

		return true;
	}




}
