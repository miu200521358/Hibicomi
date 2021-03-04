package jp.comfycolor.hibicomi.bean.info;

public class SiteInfoBean {

	private int site_id;
	private String site_name;
	private String site_affiliate_url;

	public int getSite_id() {
		return site_id;
	}
	public void setSite_id(int site_id) {
		this.site_id = site_id;
	}
	public String getSite_name() {
		return site_name;
	}
	public void setSite_name(String site_name) {
		this.site_name = site_name;
	}
	public String getSite_affiliate_url() {
		return site_affiliate_url;
	}
	public void setSite_affiliate_url(String site_affiliate_url) {
		this.site_affiliate_url = site_affiliate_url;
	}
	@Override
	public String toString() {
		return "SiteInfoBean [site_id=" + site_id + ", site_name=" + site_name + ", site_affiliate_url="
				+ site_affiliate_url + "]";
	}

}