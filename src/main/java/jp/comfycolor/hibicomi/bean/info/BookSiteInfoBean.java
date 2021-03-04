package jp.comfycolor.hibicomi.bean.info;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jp.comfycolor.hibicomi.bean.setting.ScrapingSettingBean.PageBean;
import jp.comfycolor.hibicomi.bean.setting.ScrapingSettingBean.SiteBean;

public class BookSiteInfoBean {

	private SiteInfoBean site = new SiteInfoBean();

	private Map<String, List<BookInfoBean>> book_map = new LinkedHashMap<>();

	public SiteInfoBean getSite() {
		return site;
	}

	public void setSite(SiteInfoBean site) {
		this.site = site;
	}

	public Map<String, List<BookInfoBean>> getBook_map() {
		return book_map;
	}

	public void setBook_map(Map<String, List<BookInfoBean>> book_map) {
		this.book_map = book_map;
	}

	public List<BookInfoBean> getBook_list(SiteBean site, PageBean page) {
		if (!book_map.containsKey(createBookListKey(site, page))) {
			book_map.put(createBookListKey(site, page), new ArrayList<BookInfoBean>());
		}

		return book_map.get(createBookListKey(site, page));
	}

	public void addBook_list(SiteBean site, PageBean page, BookInfoBean bookInfo) {
		if (!book_map.containsKey(createBookListKey(site, page))) {
			book_map.put(createBookListKey(site, page), new ArrayList<BookInfoBean>());
		}

		book_map.get(createBookListKey(site, page)).add(bookInfo);
	}

	private String createBookListKey(SiteBean site, PageBean page) {
		return site.getName() + ":"+ page.getName();
	}

	@Override
	public String toString() {
		return "BookSiteInfoBean [site=" + site + ", book_map=" + book_map + "]";
	}

}
