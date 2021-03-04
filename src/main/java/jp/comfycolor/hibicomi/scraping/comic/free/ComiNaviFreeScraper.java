package jp.comfycolor.hibicomi.scraping.comic.free;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import jp.comfycolor.hibicomi.bean.info.BookInfoBean;
import jp.comfycolor.hibicomi.bean.setting.ScrapingSettingBean.PageBean;
import jp.comfycolor.hibicomi.bean.setting.ScrapingSettingBean.SiteBean;
import jp.comfycolor.hibicomi.bean.setting.SettingBean;
import jp.comfycolor.hibicomi.scraping.comic.detail.ComiNaviDetailScraper;
import jp.comfycolor.hibicomi.utils.scraping.ScrapeUtils;

public class ComiNaviFreeScraper extends BaseFreeScraper {

	public ComiNaviFreeScraper(SettingBean setting, SiteBean site) {
		super(setting, site);

		detailScraper = new ComiNaviDetailScraper(driver, js, setting, site);
	}

	@Override
	public boolean scrapeUrl(PageBean page, String url) throws Exception {
		driver.get(url);
		ScrapeUtils.waitReadyStateComplete(driver, js);

		logger.debug(site.getName() +":"+ page.getName() +"<<scrape>> url: "+ driver.getCurrentUrl());

		List<BookInfoBean> currentBookInfoList = new ArrayList<BookInfoBean>();
		boolean isExistedStop = false;
		for ( WebElement ae : driver.findElements(By.className("layout-title-link")) ) {
			// 表紙画像リンクはスキップ
			if (ae.findElements(By.tagName("img")).size() > 0) {
				continue;
			}

			BookInfoBean bookInfo = new BookInfoBean(setting);

			logger.debug("■■ "
					+ ", freeEnd: "+ bookInfo.getFree_end_datetime()
					+ ", タイトル: "+ ae.getText()
					+ ", URL: "+ ae.getAttribute("href")
					);

			bookInfo.setBook_site_url(ae.getAttribute("href"));

			// 現在リストに追加
			currentBookInfoList.add(bookInfo);

			// 件数上限を超えたら終了
			if (isLimitOver(page, currentBookInfoList)) {
				break;
			}
		}

		// 現在の一覧リストから処理対象を取得し、詳細を確認する
		for (BookInfoBean bookInfo : requestDetailTargetBookInfo(currentBookInfoList)) {
			if (scrapeDetail(bookInfo)) {
				bookSiteInfo.addBook_list(site, page, bookInfo);

				// 件数上限を超えたら終了
				if (isLimitOver(page)) {
					break;
				}
			}
			else {
				logger.debug(site.getName() + ":" + page.getName() + "<<scrape終了>> url: " + driver.getCurrentUrl());
				return true;
			}
		}

		// 一覧に戻る
		driver.get(url);
		ScrapeUtils.waitReadyStateComplete(driver, js);

		if (!isExistedStop && !isLimitOver(page)) {
			// 次へのリンクが有効な状態である場合
			List<WebElement> nextEs = driver.findElements(By.xpath("//a[@class='page-next']/div/span[@style='color:#ffffff']"));
			if ( nextEs.size() > 0 ) {
				// ボタン押下のため、画面最下部にスクロールする
				js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
				ScrapeUtils.waitReadyStateComplete(driver, js);

				WebElement ae = nextEs.get(0).findElement(By.xpath("../.."));
//				if ( nextEs.get(0).findElements(By.xpath("div/span[@style='color:#ffffff']")).size() > 0 ) {
					logger.debug("nextEs: "+ ae.isDisplayed() + ", ["+ ae.getText() + "]");
					// 「次へ」押下
					logger.debug( site.getName() +":"+ page.getName() +" <<NEXT>> -----------------------------------------");
					ae.click();

					return scrapeUrl(page, driver.getCurrentUrl());
//				}
			}
		}

		return true;
	}



}
