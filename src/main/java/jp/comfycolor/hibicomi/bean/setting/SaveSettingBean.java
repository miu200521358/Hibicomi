package jp.comfycolor.hibicomi.bean.setting;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SaveSettingBean {

	@SerializedName("active")
	@Expose
	private boolean active;
	@SerializedName("exec")
	@Expose
	private String exec;

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

}
