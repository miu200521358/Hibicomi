package jp.comfycolor.hibicomi.launcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.comfycolor.hibicomi.bean.setting.SettingBean;

public abstract class BaseLauncher {

	protected static Logger logger = LoggerFactory.getLogger(BaseLauncher.class);

	protected SettingBean setting = null;

	/**
	 * コンストラクタ
	 *
	 * @param setting
	 */
	public BaseLauncher(SettingBean setting) {
		this.setting = setting;
	}

	/**
	 * ランチャメイン処理
	 *
	 * @return
	 * @throws Exception
	 */
	public abstract void execute() throws Exception;

}
