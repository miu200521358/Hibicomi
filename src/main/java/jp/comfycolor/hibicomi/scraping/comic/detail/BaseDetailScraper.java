package jp.comfycolor.hibicomi.scraping.comic.detail;

import org.apache.commons.codec.net.URLCodec;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.comfycolor.hibicomi.bean.info.BookInfoBean;
import jp.comfycolor.hibicomi.bean.setting.ScrapingSettingBean.SiteBean;
import jp.comfycolor.hibicomi.bean.setting.SettingBean;

public abstract class BaseDetailScraper {

	public static Logger logger = LoggerFactory.getLogger(BaseDetailScraper.class);

	protected WebDriver driver;
	protected JavascriptExecutor js;
	protected SettingBean setting;
	protected SiteBean site;
	protected URLCodec codec;

	public BaseDetailScraper(WebDriver driver, JavascriptExecutor js, SettingBean setting, SiteBean site) {
		super();
		this.driver = driver;
		this.js = js;
		this.setting = setting;
		this.site = site;

		codec = new URLCodec("UTF-8");
	}

	public abstract boolean scrapeUrl(BookInfoBean bookInfo) throws Exception;

}
