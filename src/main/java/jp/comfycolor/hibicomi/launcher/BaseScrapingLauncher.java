package jp.comfycolor.hibicomi.launcher;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.lang3.StringUtils;

import jp.comfycolor.hibicomi.bean.setting.ScrapingSettingBean.SiteBean;
import jp.comfycolor.hibicomi.bean.setting.SettingBean;
import jp.comfycolor.hibicomi.save.BaseSaver;
import jp.comfycolor.hibicomi.scraping.comic.BaseScraper;
import jp.comfycolor.hibicomi.utils.err.HibicomiFailureException;
import jp.comfycolor.hibicomi.utils.file.MyFileUtils;
import jp.comfycolor.hibicomi.utils.mail.MailUtils;

public class BaseScrapingLauncher extends BaseLauncher {

	/**
	 * @param setting
	 */
	public BaseScrapingLauncher(SettingBean setting) {
		super(setting);
	}

	/**
	 * ランチャメイン処理
	 *
	 * @return
	 * @throws Exception
	 */
	public void execute() throws Exception {

		// 実行時刻保持
		if ( StringUtils.isEmpty(setting.getIndividualSetting().getTargetNow()) ) {
			// 指定時刻がなければ、そのまま現在日時を設定
			setting.setNow(LocalDateTime.now());

			if (!setting.getIndividualSetting().getScrapingSetting().isActive()) {
				throw new HibicomiFailureException("実行時刻未指定で、取得処理が非実行になっています。");
			}
		}
		else {
			if (setting.getIndividualSetting().getScrapingSetting().isActive()) {
				throw new HibicomiFailureException("実行時刻指定で、取得処理が実行になっています。");
			}
		}

		// 取得処理実行
		if (setting.getIndividualSetting().getScrapingSetting().isActive()) {
			logger.info("＊Scraping 開始");
			executeScraping();
			logger.info("＊Scraping 終了");
		} else {
			logger.warn("＊Scraping 対象外");
		}

		// 保存処理実行
		if (setting.getIndividualSetting().getSaveSetting().isActive()) {
			logger.info("＊Save 開始");
			executeSave();
			logger.info("＊Save 終了");

			// 投稿済みに移動
			MyFileUtils.savePostedJsonDir(setting);
		} else {
			logger.warn("＊Save 対象外");
		}
	}

	/**
	 * スクレイピング実行
	 *
	 * @throws Exception
	 */
	private void executeScraping() throws Exception {

		// スレッドに分けて実行する
		ExecutorService es = Executors
				.newFixedThreadPool(setting.getIndividualSetting().getScrapingSetting().getThreadNum());

		try {
			// スレッド数分、実行
			ArrayList<Future<Boolean>> resultList = new ArrayList<>();

			// サイト情報取得処理
			for (SiteBean site : setting.getIndividualSetting().getScrapingSetting().getSiteList()) {
				logger.debug(site.toString());

				if (!site.isActive()) {
					logger.warn("Active 対象外 ** " + site.getName());
					continue;
				}

				// 対象クラスを読み込んで実行
				@SuppressWarnings("unchecked")
				Class<BaseScraper> clazz = (Class<BaseScraper>) Class.forName(site.getExec());

				// スクレイピング実行
				resultList.add(es.submit(
						clazz.getConstructor(SettingBean.class, SiteBean.class).newInstance(setting, site)));
			}

			// 失敗アリに、trueが入っていたらエラー
			// 結果を受け取るまで待つ
			boolean isFailure = false;
			for (Future<Boolean> r : resultList) {
				isFailure = isFailure & r.get();
			}

			if (isFailure) {
				throw new HibicomiFailureException("スクレイピング失敗");
			}

		} catch (Exception e) {
			logger.error("スクレイピング失敗", e);

			// メール通知
			MailUtils.sendException(setting.getIndividualSetting().getExecType(), setting.getMail(), e);

			// スクレイピング例外は、とりあえずこのサイト分だけ飛ばして他を進める
		} finally {
			es.shutdown();
		}
	}

	/**
	 * スクレイピング実行
	 *
	 * @throws Exception
	 */
	private void executeSave() throws Exception {

		try {
			// JSONディレクトリ
			File dir = MyFileUtils.initJsonDir(setting);

			// JSONディレクトリ内のJSONファイルを全件処理する
			// 並行させるとパンクする可能性があるので一件ずつ処理する
			for (File file : dir.listFiles()) {

				// 対象クラスを読み込んで実行
				@SuppressWarnings("unchecked")
				Class<BaseSaver> clazz = (Class<BaseSaver>) Class
						.forName(setting.getIndividualSetting().getSaveSetting().getExec());

				// 保存実行
				clazz.getConstructor(SettingBean.class, File.class).newInstance(setting, file).execute();
			}
		} catch (Exception e) {
			logger.error("保存失敗", e);

			// メール通知
			MailUtils.sendException(setting.getIndividualSetting().getExecType(), setting.getMail(), e);

			// 保存はエラーを投げて、正常終了させない
			throw e;
		}

	}

}
