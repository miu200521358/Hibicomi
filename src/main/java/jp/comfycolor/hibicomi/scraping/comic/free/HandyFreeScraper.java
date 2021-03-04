package jp.comfycolor.hibicomi.scraping.comic.free;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import jp.comfycolor.hibicomi.bean.info.BookInfoBean;
import jp.comfycolor.hibicomi.bean.setting.ScrapingSettingBean.PageBean;
import jp.comfycolor.hibicomi.bean.setting.ScrapingSettingBean.SiteBean;
import jp.comfycolor.hibicomi.bean.setting.SettingBean;
import jp.comfycolor.hibicomi.scraping.comic.detail.HandyDetailScraper;
import jp.comfycolor.hibicomi.utils.scraping.ScrapeUtils;

public class HandyFreeScraper extends BaseFreeScraper {

	public HandyFreeScraper(SettingBean setting, SiteBean site) {
		super(setting, site);

		detailScraper = new HandyDetailScraper(driver, js, setting, site);
	}

	@Override
	public boolean scrapeUrl(PageBean page, String url) throws Exception {
		driver.get(url);
		ScrapeUtils.waitReadyStateComplete(driver, js);

		logger.debug(site.getName() +":"+ page.getName() +"<<scrape>> url: "+ driver.getCurrentUrl());

		List<BookInfoBean> currentBookInfoList = new ArrayList<BookInfoBean>();
		boolean isExistedStop = false;
		for ( WebElement ae : driver.findElements(By.className("book-list__cel-link")) ) {
			BookInfoBean bookInfo = new BookInfoBean(setting);

			logger.debug("■■ "
					+ ", freeEnd: "+ bookInfo.getFree_end_datetime()
					+ ", タイトル: "+ ae.findElement(By.className("book-list__title")).getText()
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
			List<WebElement> nextEs = driver.findElements(By.xpath("//div[@class='pager__next-btn-cell']/a[text()='次へ']"));
			if ( nextEs.size() > 0 ) {
				// 「次へ」押下
				logger.debug( site.getName() +":"+ page.getName() +" <<NEXT>> -----------------------------------------");
				ScrapeUtils.scrollAndClick(driver, js, nextEs.get(0));

				return scrapeUrl(page, driver.getCurrentUrl());
			}
		}

		return true;
	}


}