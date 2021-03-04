package jp.comfycolor.hibicomi.scraping.comic.free;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import jp.comfycolor.hibicomi.bean.info.BookInfoBean;
import jp.comfycolor.hibicomi.bean.setting.ScrapingSettingBean.PageBean;
import jp.comfycolor.hibicomi.bean.setting.ScrapingSettingBean.SiteBean;
import jp.comfycolor.hibicomi.bean.setting.SettingBean;
import jp.comfycolor.hibicomi.scraping.comic.detail.MangaOkokuDetailScraper;
import jp.comfycolor.hibicomi.utils.scraping.ScrapeUtils;

public class MangaOkokuFreeScraper extends BaseFreeScraper {

	public MangaOkokuFreeScraper(SettingBean setting, SiteBean site) {
		super(setting, site);

		detailScraper = new MangaOkokuDetailScraper(driver, js, setting, site);
	}

	@Override
	public boolean scrapeUrl(PageBean page, String url) throws Exception {
		driver.get(url);
		ScrapeUtils.waitReadyStateComplete(driver, js);

		logger.debug(site.getName() +":"+ page.getName() +"<<scrape>> url: "+ driver.getCurrentUrl());

		List<BookInfoBean> currentBookInfoList = new ArrayList<BookInfoBean>();
		boolean isExistedStop = false;
		for ( WebElement ae : driver.findElements(By.className("book-list--item")) ) {
			BookInfoBean bookInfo = new BookInfoBean(setting);

			String detailUrl = StringUtils.replace(ae.getAttribute("href"), "/pv", "/vol/1");

			// 無料終了期間
			for (WebElement asideE : ae.findElements(By.tagName("aside"))) {
				if (StringUtils.contains(asideE.getText(), "まで")) {
					MonthDay md = MonthDay.parse(asideE.getText(), DateTimeFormatter.ofPattern("M/dまで"));

					// 月が現在より前の場合、来年と見なす
					int yearOffset = (md.getMonthValue() < LocalDate.now().getMonthValue()) ? 1 : 0;
					bookInfo.setFree_end_datetime(md.atYear(LocalDate.now().getYear() + yearOffset).atTime(LocalTime.MAX));
				}
			}

			logger.debug("■■ "
					+ ", タイトル: "+ ae.findElement(By.tagName("h2")).getText()
					+ ", freeEnd: "+ bookInfo.getFree_end_datetime()
					+ ", URL: "+ detailUrl
					);

			bookInfo.setBook_site_url(detailUrl);

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

		logger.debug("isExistedStop: "+ isExistedStop + ", isLimitOver(page): "+ isLimitOver(page));

		if (!isExistedStop && !isLimitOver(page)) {
			// 次へのリンクが有効な状態である場合
			List<WebElement> pagerEs = driver.findElements(By.xpath("//span[@class='paging--next']"));

			logger.debug("pageEs.size: "+ pagerEs.size());

			if (pagerEs.size() > 0) {
				// 「次へ」押下
				logger.debug("<<NEXT>> -----------------------------------------");
				ScrapeUtils.scrollAndClick(driver, js, pagerEs.get(0));

				return scrapeUrl(page, driver.getCurrentUrl());
			}
		}

		return true;
	}


}
