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
import jp.comfycolor.hibicomi.scraping.comic.detail.RakutenKoboDetailScraper;
import jp.comfycolor.hibicomi.utils.scraping.ScrapeUtils;
import jp.comfycolor.hibicomi.utils.text.BookTextUtils;

public class RakutenKoboFreeScraper extends BaseFreeScraper {

	public RakutenKoboFreeScraper(SettingBean setting, SiteBean site) {
		super(setting, site);

		detailScraper = new RakutenKoboDetailScraper(driver, js, setting, site);
	}

	@Override
	public boolean scrapeUrl(PageBean page, String url) throws Exception {
		driver.get(url);
		ScrapeUtils.waitReadyStateComplete(driver, js);

		logger.debug(site.getName() +":"+ page.getName() +"<<scrape>> url: "+ driver.getCurrentUrl());

		List<BookInfoBean> currentBookInfoList = new ArrayList<BookInfoBean>();
		boolean isExistedStop = false;
		for ( WebElement lie : driver.findElements(By.xpath("//div[@id='ratArea']/div/ul/li[@class='rbcomp__item-tile__item']")) ) {
			BookInfoBean bookInfo = new BookInfoBean(setting);
			// ジャンル(検索条件で設定)
			bookInfo.addGenre_list( BookTextUtils.formatGenre(page.getGenre(), setting.getIndividualSetting().getScrapingSetting().getGenreMap()));

			// 詳細URL取得
			WebElement detailAE = lie.findElement(By.xpath("div/h3/a"));

			logger.debug("■■ "
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
			// 次へのリンクが有効な状態である場合
			List<WebElement> pagerEs = driver.findElements(By.xpath("//div[@class='rbcomp__pager-controller']/a"));
			for (WebElement pagerE : pagerEs ) {
				if ( StringUtils.contains(pagerE.getText(), "次の")  ) {
					// 「次へ」押下
					logger.debug("<<NEXT>> -----------------------------------------");
					ScrapeUtils.scrollAndClick(driver, js, pagerE);

					return scrapeUrl(page, driver.getCurrentUrl());
				}
			}
		}

		return true;
	}


}
