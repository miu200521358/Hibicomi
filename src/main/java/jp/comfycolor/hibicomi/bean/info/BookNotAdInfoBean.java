package jp.comfycolor.hibicomi.bean.info;

import java.util.List;

public class BookNotAdInfoBean {

	private BookInfoBean book_site;
	private BookInfoBean book;
	private SiteInfoBean site;
	private List<GenreInfoBean> genres;


	public SiteInfoBean getSite() {
		return site;
	}
	public void setSite(SiteInfoBean site) {
		this.site = site;
	}
	public BookInfoBean getBook_site() {
		return book_site;
	}
	public void setBook_site(BookInfoBean book_site) {
		this.book_site = book_site;
	}
	public BookInfoBean getBook() {
		return book;
	}
	public void setBook(BookInfoBean book) {
		this.book = book;
	}
	public List<GenreInfoBean> getGenres() {
		return genres;
	}
	public void setGenres(List<GenreInfoBean> genres) {
		this.genres = genres;
	}
	@Override
	public String toString() {
		return "BookNotAdInfoBean [book_site=" + book_site + ", book=" + book + ", site=" + site + ", genres=" + genres
				+ "]";
	}



}
