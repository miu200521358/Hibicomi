package jp.comfycolor.hibicomi.bean.info;

public class GenreInfoBean {

	private int genre_id;
	private String genre_name;

	public int getGenre_id() {
		return genre_id;
	}
	public void setGenre_id(int genre_id) {
		this.genre_id = genre_id;
	}
	public String getGenre_name() {
		return genre_name;
	}
	public void setGenre_name(String genre_name) {
		this.genre_name = genre_name;
	}
	@Override
	public String toString() {
		return "SiteInfoBean [genre_id=" + genre_id + ", genre_name=" + genre_name + "]";
	}

}