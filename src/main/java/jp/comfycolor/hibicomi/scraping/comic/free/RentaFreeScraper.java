package jp.comfycolor.hibicomi.scraping.comic.free;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import jp.comfycolor.hibicomi.bean.info.BookInfoBean;
import jp.comfycolor.hibicomi.bean.setting.ScrapingSettingBean.PageBean;
import jp.comfycolor.hibicomi.bean.setting.ScrapingSettingBean.SiteBean;
import jp.comfycolor.hibicomi.bean.setting.SettingBean;
import jp.comfycolor.hibicomi.scraping.comic.detail.RentaDetailScraper;
import jp.comfycolor.hibicomi.utils.scraping.ScrapeUtils;

public class RentaFreeScraper extends BaseFreeScraper {

	public RentaFreeScraper(SettingBean setting, SiteBean site) {
		super(setting, site);

		detailScraper = new RentaDetailScraper(driver, js, setting, site);
	}

	@Override
	public boolean scrapeUrl(PageBean page, String url) throws Exception {
		driver.get(url);
		ScrapeUtils.waitReadyStateComplete(driver, js);

		logger.debug(site.getName() +":"+ page.getName() +"<<scrape>> url: "+ driver.getCurrentUrl());

		List<BookInfoBean> currentBookInfoList = new ArrayList<BookInfoBean>();

		long height = (long)js.executeScript("return window.innerHeight;");
		logger.debug("height: "+ height);

		int equalCnt = 0;
		int cnt = 0;
		int btnSize = 0;
		int newBtnSize = 0;

		comicLoop:
		while (true) {

			// 現在のコミック件数を数えておく
			newBtnSize = driver.findElements(By.className("box")).size();
			logger.debug("<<start>> newBtnSize: "+ newBtnSize +"<--> btnSize:"+ btnSize);

			// 今回取得したコミックデータをチェックする
			for (int i = btnSize; i < newBtnSize; i++ ) {
				logger.debug("box i="+ i + ", size: "+ driver.findElements(By.className("box")).size());

				WebElement dive = driver.findElements(By.className("box")).get(i);

				BookInfoBean bookInfo = new BookInfoBean(setting);

				// 詳細URL取得
				String detailUrl = dive.findElement(By.className("cover_a")).getAttribute("href");

				// 無料終了期間
				if (dive.findElements(By.className("js-campEnd")).size() > 0) {
					String freeEnd = dive.findElement(By.className("js-campEnd")).getText();
					if (StringUtils.isNotEmpty(freeEnd)) {
						if (StringUtils.equals(freeEnd, "まもなく終了")) {
							// 今日の最後を設定する
							bookInfo.setFree_end_datetime(LocalDate.now().atTime(LocalTime.MAX));
						}
						else if (StringUtils.contains(freeEnd, "まで無料")) {
							MonthDay md = MonthDay.parse(freeEnd, DateTimeFormatter.ofPattern("M/dまで無料"));

							// 月が現在より前の場合、来年と見なす
							int yearOffset = (md.getMonthValue() < LocalDate.now().getMonthValue()) ? 1 : 0;
							bookInfo.setFree_end_datetime(md.atYear(LocalDate.now().getYear() + yearOffset).atTime(LocalTime.MAX));
						}
					}
				}

				logger.debug("■■ "
						+ ", freeEnd: "+ bookInfo.getFree_end_datetime()
//						+ ", ジャンル: "+ dive.findElement(By.className("genre_color")).getText()
						+ ", タイトル: "+ dive.findElement(By.tagName("h3")).getText()
						+ ", URL: "+ detailUrl
						);

				// チェック用URLに変換
				String checkUrl = RegExUtils.replaceFirst(detailUrl, "^.*pg=", "https://renta.papy.co.jp");
				bookInfo.setBook_site_url(checkUrl);

				// 現在リストに追加
				currentBookInfoList.add(bookInfo);

				// 件数上限を超えたら終了
				if (isLimitOver(page, currentBookInfoList)) {
					break comicLoop;
				}
			}

			// ボタンの数を更新
			btnSize = newBtnSize;

			// スクロールしてコミックデータ取得
			js.executeScript("window.scrollBy(0, "+ height * cnt +")");
			ScrapeUtils.waitReadyStateComplete(driver, js);

			// スクロールした後のコミック件数を数える
			newBtnSize = driver.findElements(By.className("box")).size();
			logger.debug("<<after>> newBtnSize: "+ newBtnSize +"<--> btnSize:"+ btnSize);

			// 無料ボタンの数が変わらなければ、スクロール終了
			if (btnSize == newBtnSize) {
				// 5回試しても変わらなければ終了
				if (equalCnt > 5) {
					break comicLoop;
				}
				// 規定回数までは回す
				else {
					equalCnt++;
				}
			}
			else {
				// ボタンの数が変わっていたらクリア
				equalCnt = 0;
			}
			// スクロール回数インクリメント
			cnt++;

			ScrapeUtils.sleep(1);
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

		return true;
	}



}
