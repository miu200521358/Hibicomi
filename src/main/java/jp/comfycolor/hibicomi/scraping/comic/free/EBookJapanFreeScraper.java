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
import jp.comfycolor.hibicomi.scraping.comic.detail.EBookJapanDetailScraper;
import jp.comfycolor.hibicomi.utils.scraping.ScrapeUtils;

public class EBookJapanFreeScraper extends BaseFreeScraper {

	public EBookJapanFreeScraper(SettingBean setting, SiteBean site) {
		super(setting, site);

		detailScraper = new EBookJapanDetailScraper(driver, js, setting, site);
	}

	@Override
	public boolean scrapeUrl(PageBean page, String url) throws Exception {
		driver.get(url);
		ScrapeUtils.waitReadyStateComplete(driver, js);

		logger.debug(site.getName() +":"+ page.getName() +"<<scrape>> url: "+ driver.getCurrentUrl());

		List<BookInfoBean> currentBookInfoList = new ArrayList<BookInfoBean>();
		boolean isExistedStop = false;
		for ( WebElement dive : driver.findElements(By.xpath("//ul[@class='freeBookList clearfix']/li/div[@class='right']")) ) {
			BookInfoBean bookInfo = new BookInfoBean(setting);

			// 詳細URL取得
			WebElement detailAE = dive.findElement(By.xpath("p[@class='title']/a"));

			// 無料終了期間
			if (dive.findElements(By.xpath("p[@class='limit']")).size() > 0) {
				String freeEnd = dive.findElement(By.xpath("p[@class='limit']")).getText();
				if (StringUtils.isNotEmpty(freeEnd)) {
					if (StringUtils.contains(freeEnd, "まで無料")) {
						MonthDay md = MonthDay.parse(freeEnd, DateTimeFormatter.ofPattern("MM/ddまで無料"));

						// 月が現在より前の場合、来年と見なす
						int yearOffset = (md.getMonthValue() < LocalDate.now().getMonthValue()) ? 1 : 0;
						bookInfo.setFree_end_datetime(md.atYear(LocalDate.now().getYear() + yearOffset).atTime(LocalTime.MAX));
					}
				}
			}

			logger.debug("■■ "
					+ ", freeEnd: "+ bookInfo.getFree_end_datetime()
					+ ", タイトル: "+ detailAE.getText()
					+ ", URL: "+ detailAE.getAttribute("href")
					);

			bookInfo.setBook_site_url(detailAE.getAttribute("href"));

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
			List<WebElement> nextEs = driver.findElements(By.xpath("//li[@class='pagerList']/a[@class='pager next']"));
			if ( nextEs.size() > 0 ) {
				// 「次へ」押下
				logger.debug("<<NEXT>> -----------------------------------------");
				ScrapeUtils.scrollAndClick(driver, js, nextEs.get(0));

				return scrapeUrl(page, driver.getCurrentUrl());
			}
		}

		return true;
	}


}
