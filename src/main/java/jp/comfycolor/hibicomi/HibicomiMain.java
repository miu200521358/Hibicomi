package jp.comfycolor.hibicomi;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import jp.comfycolor.hibicomi.bean.setting.IndividualSettingBean;
import jp.comfycolor.hibicomi.bean.setting.SettingBean;
import jp.comfycolor.hibicomi.launcher.BaseLauncher;
import jp.comfycolor.hibicomi.utils.file.MyFileUtils;
import jp.comfycolor.hibicomi.utils.mail.MailUtils;

public class HibicomiMain {

	private static Logger logger = LoggerFactory.getLogger(HibicomiMain.class);

	static SettingBean setting = null;

	public static void main(String[] args) {
		logger.info("**** HibicomiMain 開始");

		logger.debug("** 引数開始");
		for (String arg : args) {
			logger.debug(arg);
		}
		logger.debug("** 引数終了");

		// 設定ファイルの展開
		// 0:distルートパス
		// 1:データディレクトリルートパス
		// 2:共通設定json
		// 3:個別設定json
		// 4:Webドライバ相対パス
		initializeSetting(args[0], args[1], args[2], args[3], args[4]);

		Gson gson = new Gson();
		logger.debug(gson.toJson(setting));

		// 処理開始
		MailUtils.sendStart(setting.getIndividualSetting().getExecType(), setting.getMail(), setting.getNow());

		try {
			@SuppressWarnings("unchecked")
			Class<BaseLauncher> clazz = (Class<BaseLauncher>) Class.forName(setting.getIndividualSetting().getExec());

			// クラスインスタンスを生成して実行
			clazz.getConstructor(SettingBean.class)	.newInstance(setting).execute();
		} catch (Exception e) {
			logger.error("Launcher失敗", e);

			// エラーメール通知
			MailUtils.sendException(setting.getIndividualSetting().getExecType(), setting.getMail(), e);
			System.exit(-1);
		}

		// 処理終了
		MailUtils.sendSuccess(setting.getIndividualSetting().getExecType(), setting.getMail(), setting.getNow());
	}

	/**
	 * 設定情報初期化
	 *
	 * @param distRootDir
	 * @param dataRootDir
	 * @param commonSettingName
	 * @param individualSettingName
	 * @param driverPath
	 */
	protected static void initializeSetting(String distRootDir, String dataRootDir, String commonSettingName, String individualSettingName, String driverPath) {
		Gson gson = new Gson();

		try {
			setting = gson.fromJson(FileUtils.readFileToString(new File(distRootDir, commonSettingName), "UTF-8"),
					SettingBean.class);
		} catch (JsonSyntaxException | IOException e) {
			logger.error("設定ファイル(共通)展開失敗", e);
			System.exit(-1);
		}

		try {
			setting.setIndividualSetting(gson.fromJson(FileUtils.readFileToString(new File(distRootDir, individualSettingName), "UTF-8"),
					IndividualSettingBean.class));
		} catch (JsonSyntaxException | IOException e) {
			logger.error("設定ファイル(個別)展開失敗", e);
			System.exit(-1);
		}

		// データ用ディレクトリは環境によるので引数でもらう
		setting.getDir().setRootDir(dataRootDir);

		// dist用ディレクトリは環境によるので引数でもらう
		setting.getDir().setDistRrootDir(distRootDir);

		// Webドライバパスは環境によるので引数でもらう
		setting.getIndividualSetting().getScrapingSetting().setDriverPath(new File(distRootDir, driverPath));

		// 実行時刻保持
		if ( StringUtils.isEmpty(setting.getIndividualSetting().getTargetNow()) ) {
			// 指定時刻がなければ、そのまま現在日時を設定
			setting.setNow(LocalDateTime.now());
		}
		else {
			// 時刻が指定されていたら、それを利用
			try {
				setting.setNow(LocalDateTime.parse(setting.getIndividualSetting().getTargetNow(), MyFileUtils.NOW_DIR_FORMATTER));
			}
			catch (Exception e) {
				logger.error("処理対象時刻展開失敗: "+ setting.getIndividualSetting().getTargetNow(), e);
				System.exit(-1);
			}
		}
	}


}
