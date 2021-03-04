package jp.comfycolor.hibicomi.bean.setting;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ScrapingSettingBean {

	// Webドライバフルパス
	private File driverPath;

	@SerializedName("active")
	@Expose
	private boolean active;
	@SerializedName("detailPauseSecond")
	@Expose
	private int detailPauseSecond;
	@SerializedName("threadNum")
	@Expose
	private int threadNum;
	@SerializedName("limit")
	@Expose
	private int limit;
	@SerializedName("ua")
	@Expose
	private String ua;
	@SerializedName("siteList")
	@Expose
	private List<SiteBean> siteList = null;
	@SerializedName("catchcopyResources")
	@Expose
	private List<String> catchcopyResources = null;
	@SerializedName("genreMap")
	@Expose
	private HashMap<String, List<String>> genreMap = null;
	@SerializedName("isExistedStop")
	@Expose
	private boolean isExistedStop;
	@SerializedName("isExistedSkip")
	@Expose
	private boolean isExistedSkip;
	@SerializedName("isCheckFreeEnd")
	@Expose
	private boolean isCheckFreeEnd;


	/**
	 * キャッチコピーをランダムに取得する
	 *
	 * @return
	 */
	public String findRandomCatchCopy() {
		return catchcopyResources.get((int) Math.floor(Math.random() * catchcopyResources.size()));
	}

	@Override
	public String toString() {
		return "ScrapingSettingBean [driverPath=" + driverPath + ", active=" + active + ", detailPauseSecond="
				+ detailPauseSecond + ", threadNum=" + threadNum + ", limit=" + limit + ", ua=" + ua + ", siteList="
				+ siteList + ", catchcopyResources=" + catchcopyResources + ", genreMap=" + genreMap
				+ ", isExistedStop=" + isExistedStop + ", isExistedSkip=" + isExistedSkip + ", isCheckFreeEnd="
				+ isCheckFreeEnd + "]";
	}

	public HashMap<String, List<String>> getGenreMap() {
		return genreMap;
	}

	public void setGenreMap(HashMap<String, List<String>> genreMap) {
		this.genreMap = genreMap;
	}

	public List<String> getCatchcopyResources() {
		return catchcopyResources;
	}

	public void setCatchcopyResources(List<String> catchcopyResources) {
		this.catchcopyResources = catchcopyResources;
	}

	public File getDriverPath() {
		return driverPath;
	}

	public void setDriverPath(File driverPath) {
		this.driverPath = driverPath;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public int getDetailPauseSecond() {
		return detailPauseSecond;
	}

	public void setDetailPauseSecond(int detailPauseSecond) {
		this.detailPauseSecond = detailPauseSecond;
	}

	public int getThreadNum() {
		return threadNum;
	}

	public void setThreadNum(int threadNum) {
		this.threadNum = threadNum;
	}

	public String getUa() {
		return ua;
	}

	public void setUa(String ua) {
		this.ua = ua;
	}

	public List<SiteBean> getSiteList() {
		return siteList;
	}

	public void setSiteList(List<SiteBean> siteList) {
		this.siteList = siteList;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public boolean isExistedStop() {
		return isExistedStop;
	}

	public void setExistedStop(boolean isExistedStop) {
		this.isExistedStop = isExistedStop;
	}

	public boolean isExistedSkip() {
		return isExistedSkip;
	}

	public void setExistedSkip(boolean isExistedSkip) {
		this.isExistedSkip = isExistedSkip;
	}

	public boolean isCheckFreeEnd() {
		return isCheckFreeEnd;
	}

	public void setCheckFreeEnd(boolean isCheckFreeEnd) {
		this.isCheckFreeEnd = isCheckFreeEnd;
	}

	public class SiteBean {

		@SerializedName("name")
		@Expose
		private String name;
		@SerializedName("active")
		@Expose
		private boolean active;
		@SerializedName("exec")
		@Expose
		private String exec;
		@SerializedName("detailUrlRoot")
		@Expose
		private String detailUrlRoot;
		@SerializedName("detailUrl")
		@Expose
		private String detailUrl;
		@SerializedName("affiliateUrl")
		@Expose
		private String affiliateUrl;
		@SerializedName("imageUrl")
		@Expose
		private String imageUrl;
		@SerializedName("pageList")
		@Expose
		private List<PageBean> pageList = null;
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public boolean isActive() {
			return active;
		}
		public void setActive(boolean active) {
			this.active = active;
		}
		public String getExec() {
			return exec;
		}
		public void setExec(String exec) {
			this.exec = exec;
		}
		public String getDetailUrlRoot() {
			return detailUrlRoot;
		}
		public void setDetailUrlRoot(String detailUrlRoot) {
			this.detailUrlRoot = detailUrlRoot;
		}
		public String getAffiliateUrl() {
			return affiliateUrl;
		}
		public void setAffiliateUrl(String affiliateUrl) {
			this.affiliateUrl = affiliateUrl;
		}
		public List<PageBean> getPageList() {
			return pageList;
		}
		public void setPageList(List<PageBean> pageList) {
			this.pageList = pageList;
		}
		public String getImageUrl() {
			return imageUrl;
		}
		public void setImageUrl(String imageUrl) {
			this.imageUrl = imageUrl;
		}
		public String getDetailUrl() {
			return detailUrl;
		}
		public void setDetailUrl(String detailUrl) {
			this.detailUrl = detailUrl;
		}

	}

	public class PageBean {

		@SerializedName("name")
		@Expose
		private String name;
		@SerializedName("type")
		@Expose
		private String type;
		@SerializedName("genre")
		@Expose
		private String genre;
		@SerializedName("urlList")
		@Expose
		private List<String> urlList = null;

		public String getGenre() {
			return genre;
		}

		public void setGenre(String genre) {
			this.genre = genre;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public List<String> getUrlList() {
			return urlList;
		}

		public void setUrlList(List<String> urlList) {
			this.urlList = urlList;
		}

	}
}
