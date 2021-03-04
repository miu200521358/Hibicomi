package jp.comfycolor.hibicomi.utils.file;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import jp.comfycolor.hibicomi.bean.setting.SettingBean;
import jp.comfycolor.hibicomi.bean.setting.SettingBean.DirBean;
import jp.comfycolor.hibicomi.utils.bean.LocalDateTimeAdapter;

public class MyFileUtils {

	public static Logger logger = LoggerFactory.getLogger(MyFileUtils.class);

	public static final DateTimeFormatter NOW_DIR_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

	public static final DateTimeFormatter JSON_DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	public static final DateTimeFormatter TWITTER_YM_FORMATTER = DateTimeFormatter.ofPattern("MM/dd HH:mm");
	/**
	 * BookInfo用のGson生成
	 *
	 * @return
	 */
	public static Gson createBookInfoGson() {
		Gson gson = new GsonBuilder()
				.registerTypeAdapter(LocalDateTime.class, LocalDateTimeAdapter.create(()->JSON_DATETIME_FORMATTER))
				.setPrettyPrinting().create();

		return gson;
	}

	public static File initJsonDir(SettingBean setting) throws IOException {
		return initDir(setting.getDir(), setting.getNow(), setting.getIndividualSetting().getExecType(), setting.getDir().getJsonDir());
	}

	/**
	 * サブディレクトリの初期化
	 *
	 * @param setting
	 * @param subDir
	 * @return
	 * @throws IOException
	 */
	public static File initDir(DirBean dir, LocalDateTime now, String typeDir, String subDir) throws IOException {
		// 再構築なしで初期化
		return initDir(dir, now, typeDir, subDir, false);
	}

	/**
	 * サブディレクトリの初期化
	 *
	 * @param setting
	 * @param subDir
	 * @return
	 * @throws IOException
	 */
	public static File initDir(DirBean dir, LocalDateTime now, String typeDir, String subDir, boolean isRestore) throws IOException {

		// ルート/タイプ/日時/サブディレクトリ
		File newDir = new File(getRootDir(dir, now, typeDir).getAbsolutePath(), subDir);

		logger.debug("initDir: "+ newDir.getAbsolutePath());

		if (isRestore) {
			// リストアする場合は一旦削除
			FileUtils.deleteDirectory(newDir);
		}

		FileUtils.forceMkdir(newDir);

		return newDir;
	}

	/**
	 * 投稿の完了したディレクトリを別保存
	 */
	public static void savePostedJsonDir(SettingBean setting) throws IOException {
		savePostedDir(setting.getDir(), setting.getNow(), setting.getIndividualSetting().getExecType(), setting.getDir().getPostedDir());
	}

	/**
	 * 投稿の完了したディレクトリを別保存
	 */
	public static void savePostedDir(DirBean dir, LocalDateTime now, String typeDir, String postedDir) throws IOException {

		// ルート/日時(初期ディレクトリ)
		File src = getRootDir(dir, now, typeDir);

		// 投稿完了ディレクトリ
		File destDir = new File(FilenameUtils.normalize(
				dir.getRootDir()
				+ File.separator
				+ typeDir
				+ File.separator
				+ postedDir
			));

		logger.debug("savePostedDir ="+ src.getAbsolutePath() + " -> "+ destDir.getAbsolutePath());

		// 初期ディレクトリ -> 投稿完了ディレクトリに移動
		// 投稿完了ディレクトリは作成する
		FileUtils.moveDirectoryToDirectory(src, destDir, true);
	}


	private static File getRootDir(DirBean dir, LocalDateTime now, String typeDir) {
		return new File(FilenameUtils.normalize(
				dir.getRootDir()
				+ File.separator
				+ typeDir
				+ File.separator
				+ now.format(NOW_DIR_FORMATTER)
			));
	}

}
