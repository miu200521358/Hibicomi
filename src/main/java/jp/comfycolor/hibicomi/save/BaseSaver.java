package jp.comfycolor.hibicomi.save;

import java.io.File;

import jp.comfycolor.hibicomi.bean.setting.SettingBean;

public abstract class BaseSaver {

	protected SettingBean setting;
	protected File dataFile;

	public BaseSaver(SettingBean setting, File dataFile) throws Exception {
		this.setting = setting;
		this.dataFile = dataFile;
	}

	/**
	 * 保存処理実行
	 *
	 * @return
	 */
	public abstract boolean execute() throws Exception;
}
