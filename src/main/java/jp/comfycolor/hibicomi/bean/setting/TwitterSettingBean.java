package jp.comfycolor.hibicomi.bean.setting;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TwitterSettingBean {
	@SerializedName("consumerKey")
	@Expose
	private String consumerKey;
	@SerializedName("consumerSecret")
	@Expose
	private String consumerSecret;
	@SerializedName("accessToken")
	@Expose
	private String accessToken;
	@SerializedName("accessTokenSecret")
	@Expose
	private String accessTokenSecret;
	@SerializedName("template")
	@Expose
	private String template;
	@SerializedName("queryList")
	@Expose
	private List<String> queryList;

	public String getConsumerKey() {
		return consumerKey;
	}
	public void setConsumerKey(String consumerKey) {
		this.consumerKey = consumerKey;
	}
	public String getConsumerSecret() {
		return consumerSecret;
	}
	public void setConsumerSecret(String consumerSecret) {
		this.consumerSecret = consumerSecret;
	}
	public String getAccessToken() {
		return accessToken;
	}
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	public String getAccessTokenSecret() {
		return accessTokenSecret;
	}
	public void setAccessTokenSecret(String accessTokenSecret) {
		this.accessTokenSecret = accessTokenSecret;
	}
	public String getTemplate() {
		return template;
	}
	public void setTemplate(String template) {
		this.template = template;
	}
	public List<String> getQueryList() {
		return queryList;
	}
	public void setQueryList(List<String> queryList) {
		this.queryList = queryList;
	}



}
