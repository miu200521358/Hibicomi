package jp.comfycolor.hibicomi.bean.info;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jp.comfycolor.hibicomi.bean.setting.SettingBean;

public class BookInfoBean {
	private boolean detail_scrape;

	private long book_id;
	private String title;
	private String series_title;
	private String title_number;
	private int complete_flg;
	private String author;
	private String description;
	private String catchcopy;

	private String book_site_id;
	private String book_site_url;
	private String book_site_image;
	private String book_site_title;
	private LocalDateTime publish_start_datetime;
	private LocalDateTime publish_end_datetime;
	private LocalDateTime free_start_datetime;
	private LocalDateTime free_end_datetime;
	private LocalDateTime scraped_datetime;

	private List<String> tag_list = new ArrayList<String>();
	private List<String> genre_list = new ArrayList<String>();

	public BookInfoBean(SettingBean setting) {
		this.scraped_datetime = setting.getNow().withHour(0).withMinute(0).withSecond(0).withNano(0);
	}

	public void setComplete_flg(boolean complete_flg) {
		if (complete_flg) {
			this.complete_flg = 1;
		}
		else {
			this.complete_flg = 0;
		}
	}

	public void addTag_list(String tag) {
		tag_list.add(tag);
	}

	public void addGenre_list(String genre) {
		genre_list.add(genre);
	}

	public boolean isDetail_scrape() {
		return detail_scrape;
	}

	public void setDetail_scrape(boolean detail_scrape) {
		this.detail_scrape = detail_scrape;
	}

	public long getBook_id() {
		return book_id;
	}

	public void setBook_id(long book_id) {
		this.book_id = book_id;
	}

	public List<String> getTag_list() {
		return tag_list;
	}
	public void setTag_list(List<String> tag_list) {
		this.tag_list = tag_list;
	}
	public List<String> getGenre_list() {
		return genre_list;
	}
	public void setGenre_list(List<String> genre_list) {
		this.genre_list = genre_list;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSeries_title() {
		return series_title;
	}
	public void setSeries_title(String series_title) {
		this.series_title = series_title;
	}
	public String getTitle_number() {
		return title_number;
	}
	public void setTitle_number(String title_number) {
		this.title_number = title_number;
	}
	public int getComplete_flg() {
		return complete_flg;
	}

	public void setComplete_flg(int complete_flg) {
		this.complete_flg = complete_flg;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getCatchcopy() {
		return catchcopy;
	}
	public void setCatchcopy(String catchcopy) {
		this.catchcopy = catchcopy;
	}
	public String getBook_site_url() {
		return book_site_url;
	}

	public void setBook_site_url(String book_site_url) {
		this.book_site_url = book_site_url;
	}

	public String getBook_site_image() {
		return book_site_image;
	}
	public void setBook_site_image(String book_site_image) {
		this.book_site_image = book_site_image;
	}
	public String getBook_site_title() {
		return book_site_title;
	}
	public void setBook_site_title(String book_site_title) {
		this.book_site_title = book_site_title;
	}
	public LocalDateTime getFree_start_datetime() {
		return free_start_datetime;
	}
	public void setFree_start_datetime(LocalDateTime free_start_datetime) {
		this.free_start_datetime = free_start_datetime;
	}
	public LocalDateTime getFree_end_datetime() {
		return free_end_datetime;
	}
	public void setFree_end_datetime(LocalDateTime free_end_datetime) {
		this.free_end_datetime = free_end_datetime;
	}

	public String getBook_site_id() {
		return book_site_id;
	}

	public void setBook_site_id(String book_site_id) {
		this.book_site_id = book_site_id;
	}

	public LocalDateTime getPublish_start_datetime() {
		return publish_start_datetime;
	}

	public void setPublish_start_datetime(LocalDateTime publish_start_datetime) {
		this.publish_start_datetime = publish_start_datetime;
	}

	public LocalDateTime getPublish_end_datetime() {
		return publish_end_datetime;
	}

	public void setPublish_end_datetime(LocalDateTime publish_end_datetime) {
		this.publish_end_datetime = publish_end_datetime;
	}

	public LocalDateTime getScraped_datetime() {
		return scraped_datetime;
	}

	public void setScraped_datetime(LocalDateTime scraped_datetime) {
		this.scraped_datetime = scraped_datetime;
	}

	@Override
	public String toString() {
		return "BookInfoBean [detail_scrape=" + detail_scrape + ", book_id=" + book_id + ", title=" + title
				+ ", series_title=" + series_title + ", title_number=" + title_number + ", complete_flg="
				+ complete_flg + ", author=" + author + ", description=" + description + ", catchcopy=" + catchcopy
				+ ", book_site_id=" + book_site_id + ", book_site_url=" + book_site_url + ", book_site_image="
				+ book_site_image + ", book_site_title=" + book_site_title + ", publish_start_datetime="
				+ publish_start_datetime + ", publish_end_datetime=" + publish_end_datetime
				+ ", free_start_datetime=" + free_start_datetime + ", free_end_datetime=" + free_end_datetime
				+ ", scraped_datetime=" + scraped_datetime + ", tag_list=" + tag_list + ", genre_list=" + genre_list
				+ "]";
	}


}