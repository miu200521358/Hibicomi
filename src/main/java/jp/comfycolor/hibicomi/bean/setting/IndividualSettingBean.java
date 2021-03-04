package jp.comfycolor.hibicomi.bean.setting;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class IndividualSettingBean {
	@SerializedName("exec")
	@Expose
	private String exec;

	@SerializedName("targetNow")
	@Expose
	private String targetNow;

	@SerializedName("separate")
	@Expose
	private int separate;

	@SerializedName("targetGenreList")
	@Expose
	private List<Integer> targetGenreList;

	// スクレイピング情報
	@SerializedName("scrapingSetting")
	@Expose
	private ScrapingSettingBean scrapingSetting;

	// 保存情報
	@SerializedName("saveSetting")
	@Expose
	private SaveSettingBean saveSetting;

	// Twitter情報
	@SerializedName("twitterSetting")
	@Expose
	private TwitterSettingBean twitterSetting;


	/**
	 * execTypeはランチャのクラス名とする
	 *
	 * @return
	 */
	public String getExecType() {
		return StringUtils.substringBeforeLast(StringUtils.substringAfterLast(this.exec, "."), "Launcher");
	}

	public String getExec() {
		return exec;
	}

	public void setExec(String exec) {
		this.exec = exec;
	}

	public ScrapingSettingBean getScrapingSetting() {
		return scrapingSetting;
	}

	public void setScrapingSetting(ScrapingSettingBean scrapingSetting) {
		this.scrapingSetting = scrapingSetting;
	}

	public SaveSettingBean getSaveSetting() {
		return saveSetting;
	}

	public void setSaveSetting(SaveSettingBean saveSetting) {
		this.saveSetting = saveSetting;
	}

	public String getTargetNow() {
		return targetNow;
	}

	public void setTargetNow(String targetNow) {
		this.targetNow = targetNow;
	}

	public int getSeparate() {
		return separate;
	}

	public void setSeparate(int separate) {
		this.separate = separate;
	}

	public List<Integer> getTargetGenreList() {
		return targetGenreList;
	}

	public void setTargetGenreList(List<Integer> targetGenreList) {
		this.targetGenreList = targetGenreList;
	}

	public TwitterSettingBean getTwitterSetting() {
		return twitterSetting;
	}

	public void setTwitterSetting(TwitterSettingBean twitterSetting) {
		this.twitterSetting = twitterSetting;
	}

}
