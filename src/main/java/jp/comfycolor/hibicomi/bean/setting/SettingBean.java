package jp.comfycolor.hibicomi.bean.setting;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

public class SettingBean {
	// 実行時刻
	private LocalDateTime now;
	// ディレクトリ情報Bean
	private DirBean dir;
	// メール情報Bean
	private MailBean mail;
	// Web情報Bean
	private WebBean web;
	// 個別設定情報
	private IndividualSettingBean individualSetting;

	/**
	 * 実行時刻をDate型に変換する
	 *
	 * @return
	 */
	public Date createNowDate() {
		ZoneId zone = ZoneId.systemDefault();
		ZonedDateTime zonedDateTime = ZonedDateTime.of(now, zone);

		Instant instant = zonedDateTime.toInstant();
		return Date.from(instant);
	}

	public WebBean getWeb() {
		return web;
	}

	public void setWeb(WebBean web) {
		this.web = web;
	}

	public LocalDateTime getNow() {
		return now;
	}

	public void setNow(LocalDateTime now) {
		this.now = now;
	}

	public DirBean getDir() {
		return dir;
	}

	public void setDir(DirBean dir) {
		this.dir = dir;
	}

	public MailBean getMail() {
		return mail;
	}

	public void setMail(MailBean mail) {
		this.mail = mail;
	}

	public IndividualSettingBean getIndividualSetting() {
		return individualSetting;
	}

	public void setIndividualSetting(IndividualSettingBean individualSetting) {
		this.individualSetting = individualSetting;
	}

	public class DirBean {
		// ルートディレクトリ
		private String rootDir;
		// distルートディレクトリ
		private String distRrootDir;
		// オリジナルHTML保存ディレクトリ
		private String originalDir;
		// JSON保存ディレクトリ
		private String jsonDir;
		// 投稿完了ディレクトリ
		private String postedDir;

		public String getDistRrootDir() {
			return distRrootDir;
		}

		public void setDistRrootDir(String distRrootDir) {
			this.distRrootDir = distRrootDir;
		}

		public String getRootDir() {
			return rootDir;
		}

		public void setRootDir(String rootDir) {
			this.rootDir = rootDir;
		}

		public String getOriginalDir() {
			return originalDir;
		}

		public void setOriginalDir(String originalDir) {
			this.originalDir = originalDir;
		}

		public String getJsonDir() {
			return jsonDir;
		}

		public void setJsonDir(String jsonDir) {
			this.jsonDir = jsonDir;
		}

		public String getPostedDir() {
			return postedDir;
		}

		public void setPostedDir(String postedDir) {
			this.postedDir = postedDir;
		}

	}

	public class MailBean {
		private String id;
		private String passwd;
		private String fromAddress;
		private String fromName;
		private String toAddress;
		private String toName;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getPasswd() {
			return passwd;
		}

		public void setPasswd(String passwd) {
			this.passwd = passwd;
		}

		public String getFromAddress() {
			return fromAddress;
		}

		public void setFromAddress(String fromAddress) {
			this.fromAddress = fromAddress;
		}

		public String getFromName() {
			return fromName;
		}

		public void setFromName(String fromName) {
			this.fromName = fromName;
		}

		public String getToAddress() {
			return toAddress;
		}

		public void setToAddress(String toAddress) {
			this.toAddress = toAddress;
		}

		public String getToName() {
			return toName;
		}

		public void setToName(String toName) {
			this.toName = toName;
		}

	}

	public class WebBean {
		private String rootUrl;
		private String bearerToken;
		private String siteInfoPath;
		private String bookSiteInfoPath;
		private String bookSiteListExistedInfoPath;
		private String bookSiteListSaveInfoPath;
		private String bookNotAdPath;

		/**
		 * リクエストヘッダに設定できるBearer
		 *
		 * @return
		 */
		public String getBearerTokenHeader() {
			return "Bearer "+ bearerToken;
		}

		/**
		 * サイト情報のURL形式
		 * @return
		 */
		public String getSiteInfoUrl() {
			return rootUrl + siteInfoPath;
		}

		/**
		 * サイト別書籍情報のURL形式
		 * @return
		 */
		public String getBookSiteInfoUrl() {
			return rootUrl + bookSiteInfoPath;
		}

		/**
		 * サイト別書籍リスト情報のURL形式
		 * @return
		 */
		public String getBookSiteListExistedInfoUrl() {
			return rootUrl + bookSiteListExistedInfoPath;
		}

		/**
		 * サイト別書籍リスト保存情報のURL形式
		 * @return
		 */
		public String getBookSiteListSaveInfoUrl() {
			return rootUrl + bookSiteListSaveInfoPath;
		}

		/**
		 * 未広告書籍情報取得URL形式
		 * @return
		 */
		public String getBookNotAdUrl() {
			return rootUrl + bookNotAdPath;
		}

		public String getRootUrl() {
			return rootUrl;
		}
		public void setRootUrl(String rootUrl) {
			this.rootUrl = rootUrl;
		}

		public String getSiteInfoPath() {
			return siteInfoPath;
		}

		public void setSiteInfoPath(String siteInfoPath) {
			this.siteInfoPath = siteInfoPath;
		}

		public String getBearerToken() {
			return bearerToken;
		}

		public void setBearerToken(String bearerToken) {
			this.bearerToken = bearerToken;
		}

		public String getBookSiteInfoPath() {
			return bookSiteInfoPath;
		}

		public void setBookSiteInfoPath(String bookSiteInfoPath) {
			this.bookSiteInfoPath = bookSiteInfoPath;
		}

		public String getBookSiteListExistedInfoPath() {
			return bookSiteListExistedInfoPath;
		}

		public void setBookSiteListExistedInfoPath(String bookSiteListExistedInfoPath) {
			this.bookSiteListExistedInfoPath = bookSiteListExistedInfoPath;
		}

		public String getBookSiteListSaveInfoPath() {
			return bookSiteListSaveInfoPath;
		}

		public void setBookSiteListSaveInfoPath(String bookSiteListSaveInfoPath) {
			this.bookSiteListSaveInfoPath = bookSiteListSaveInfoPath;
		}

		public String getBookNotAdPath() {
			return bookNotAdPath;
		}

		public void setBookNotAdPath(String bookNotAdPath) {
			this.bookNotAdPath = bookNotAdPath;
		}

	}

}
