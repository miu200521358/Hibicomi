package jp.comfycolor.hibicomi.scraping.comic.free;

import jp.comfycolor.hibicomi.bean.setting.ScrapingSettingBean.SiteBean;
import jp.comfycolor.hibicomi.bean.setting.SettingBean;
import jp.comfycolor.hibicomi.scraping.comic.BaseScraper;

public abstract class BaseFreeScraper extends BaseScraper {

	public BaseFreeScraper(SettingBean setting, SiteBean site) {
		super(setting, site);
	}

}
