package jp.comfycolor.hibicomi.scraping.comic.free;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import jp.comfycolor.hibicomi.bean.info.BookInfoBean;
import jp.comfycolor.hibicomi.bean.setting.ScrapingSettingBean.PageBean;
import jp.comfycolor.hibicomi.bean.setting.ScrapingSettingBean.SiteBean;
import jp.comfycolor.hibicomi.bean.setting.SettingBean;
import jp.comfycolor.hibicomi.scraping.comic.detail.MechaComiDetailScraper;
import jp.comfycolor.hibicomi.utils.scraping.ScrapeUtils;

public class MechaComiFreeScraper extends BaseFreeScraper {

	public MechaComiFreeScraper(SettingBean setting, SiteBean site) {
		super(setting, site);

		detailScraper = new MechaComiDetailScraper(driver, js, setting, site);
	}

	@Override
	public boolean scrapeUrl(PageBean page, String url) throws Exception {
		driver.get(url);
		ScrapeUtils.waitReadyStateComplete(driver, js);

		logger.debug(site.getName() + ":" + page.getName() + "<<scrape>> url: " + driver.getCurrentUrl());

		List<BookInfoBean> currentBookInfoList = new ArrayList<BookInfoBean>();
		boolean isExistedStop = false;
		for (WebElement ae : driver.findElements(By.xpath("//span[@class='title']/a"))) {
			BookInfoBean bookInfo = new BookInfoBean(setting);

			logger.debug("■■ "
					+ ", freeEnd: " + bookInfo.getFree_end_datetime()
					+ ", タイトル: " + ae.getText()
					+ ", URL: " + ae.getAttribute("href"));

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
			List<WebElement> nextEs = driver.findElements(By.xpath("//a[@class='next_page']/span[text()='次']"));
			if (nextEs.size() > 0) {
				if (StringUtils.equals(nextEs.get(0).getAttribute("class"), "disabled")) {
					logger.debug("次へ無効");
				}
				else {
					// 「次へ」押下
					logger.debug("<<NEXT>> -----------------------------------------");
					ScrapeUtils.scrollAndClick(driver, js, nextEs.get(0));

					return scrapeUrl(page, driver.getCurrentUrl());
				}
			}
		}

		return true;
	}

}
