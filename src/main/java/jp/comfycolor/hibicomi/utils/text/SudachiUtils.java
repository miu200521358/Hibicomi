package jp.comfycolor.hibicomi.utils.text;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.worksap.nlp.sudachi.Dictionary;
import com.worksap.nlp.sudachi.DictionaryFactory;
import com.worksap.nlp.sudachi.Morpheme;
import com.worksap.nlp.sudachi.Tokenizer;

import jp.comfycolor.hibicomi.bean.setting.SettingBean;
import jp.comfycolor.hibicomi.utils.err.HibicomiFailureException;

public class SudachiUtils {

	public static Logger logger = LoggerFactory.getLogger(SudachiUtils.class);

    private static SudachiUtils singleton;

	private Dictionary dict;
	private SettingBean setting;

	public SudachiUtils (SettingBean setting) {
		this.setting = setting;

		try {
			// sudachiの設定を読み込む
			String sudachiSetting = IOUtils.resourceToString("/sudachi.json", StandardCharsets.UTF_8);
			// 辞書の場所をフルパスで指定する
			sudachiSetting = StringUtils.replace(sudachiSetting, "{distRrootDir}", setting.getDir().getDistRrootDir());
			sudachiSetting = StringUtils.replace(sudachiSetting, "\\", "\\\\");

//			logger.debug(sudachiSetting);

			this.dict = new DictionaryFactory().create(sudachiSetting);
		} catch (IOException e) {
		    throw new HibicomiFailureException("createCatchcopy辞書生成失敗", e);
		}
	}

    public static SudachiUtils getInstance(SettingBean setting){
    	if (singleton == null) {
    		singleton = new SudachiUtils(setting);
    	}

        return singleton;
    }

	/**
	 * キャッチコピー生成
	 *
	 * @param setting
	 * @param description
	 * @return
	 */
	public String createCatchcopy(String description) {
		if (StringUtils.isEmpty(description)) {
			return null;
		}

		Tokenizer tokenizer = this.dict.create();
		List<Morpheme> tokens = tokenizer.tokenize(Tokenizer.SplitMode.C, description);

		List<Morpheme> meishiMorphemes = new ArrayList<Morpheme>();
		List<Morpheme> doshiMorphemes = new ArrayList<Morpheme>();
		List<Morpheme> keiyoshiMorphemes = new ArrayList<Morpheme>();

		for (Morpheme morpheme : tokens) {
//		    logger.debug(morpheme.surface() + "\t" + morpheme.normalizedForm() + "\t" + morpheme.partOfSpeech());

		    boolean isMeishi = false;
		    boolean isDoshi = false;
		    boolean isKeiyoshi = false;
		    for (String part : morpheme.partOfSpeech()) {

		    	if (StringUtils.equals(part, "名詞")) {
		    		isMeishi = true;
		    		continue;
		    	}
		    	if (isMeishi) {
			    	if (StringUtils.equals(part, "普通名詞")) {
			    		meishiMorphemes.add(morpheme);
			    	}
			    	if (StringUtils.equals(part, "固有名詞")) {
			    		meishiMorphemes.add(morpheme);
			    	}
		    	}


		    	if (StringUtils.equals(part, "動詞")) {
		    		isDoshi = true;
		    		continue;
		    	}
		    	if (isDoshi) {
			    	if (StringUtils.equals(part, "非自立可能")) {
			    		doshiMorphemes.add(morpheme);
			    	}
			    	if (StringUtils.equals(part, "一般")) {
			    		doshiMorphemes.add(morpheme);
			    	}
		    	}

		    	if (StringUtils.equals(part, "形容詞")) {
		    		isKeiyoshi = true;
		    		continue;
		    	}
		    	if (isKeiyoshi) {
			    	if (StringUtils.equals(part, "非自立可能")) {
			    		keiyoshiMorphemes.add(morpheme);
			    	}
			    	if (StringUtils.equals(part, "一般")) {
			    		keiyoshiMorphemes.add(morpheme);
			    	}
		    	}
		    }
		}

		// とりあえず空文字無能コピー設定
		String catchcopy = "";

		// 動詞解析
		List<String> doushiTokens = getRandomeList(doshiMorphemes);

		// 名詞解析
		List<String> meishiTokens = getRandomeList(meishiMorphemes);

		// 形容詞解析
		List<String> keiyoushiTokens = getRandomeList(keiyoshiMorphemes);

		catchcopy = setting.getIndividualSetting().getScrapingSetting().findRandomCatchCopy();

		int copyMNum = 0;
		int copyKNum = 0;
		int copyDNum = 0;

		// 名詞置換
		while (StringUtils.indexOf(catchcopy, "{m}") >= 0 && copyMNum < meishiTokens.size()) {
			catchcopy = StringUtils.replaceOnce(catchcopy, "{m}", meishiTokens.get(copyMNum++));
		}

		// 形容詞置換
		while (StringUtils.indexOf(catchcopy, "{k}") >= 0 && copyKNum < keiyoushiTokens.size()) {
			catchcopy = StringUtils.replaceOnce(catchcopy, "{k}", keiyoushiTokens.get(copyKNum++));
		}
		// 形容詞が足りない場合、名詞で置換
		while (StringUtils.indexOf(catchcopy, "{k}") >= 0 && copyMNum < meishiTokens.size()) {
			catchcopy = StringUtils.replaceOnce(catchcopy, "{k}", meishiTokens.get(copyMNum++));
		}

		// 動詞置換
		while (StringUtils.indexOf(catchcopy, "{d}") >= 0 && copyDNum < doushiTokens.size()) {
			catchcopy = StringUtils.replaceOnce(catchcopy, "{d}", doushiTokens.get(copyDNum++));
		}
		// 動詞が足りない場合、名詞で置換
		while (StringUtils.indexOf(catchcopy, "{d}") >= 0 && copyMNum < meishiTokens.size()) {
			catchcopy = StringUtils.replaceOnce(catchcopy, "{d}", meishiTokens.get(copyMNum++));
		}

		logger.debug("catchcopy: "+ catchcopy);

		return catchcopy;
	}

	private ArrayList<String> getRandomeList(List<Morpheme> list) {
		ArrayList<String> randomList = new ArrayList<>();

		HashMap<String, Morpheme> map = new HashMap<>();
		for (Morpheme morpheme : list) {
			if (!map.containsKey(morpheme.normalizedForm())) {
				// まだ登録されていない語句の場合
				if (StringUtils.length(morpheme.normalizedForm()) == 1 && isKanji(morpheme.normalizedForm())) {
					// 一文字の場合、漢字であること
					map.put(morpheme.normalizedForm(), morpheme);
					randomList.add(morpheme.normalizedForm());
				} else if (StringUtils.length(morpheme.normalizedForm()) > 1) {
					// 2文字以上は登録
					map.put(morpheme.normalizedForm(), morpheme);
					randomList.add(morpheme.normalizedForm());
				}
			}
		}

		// シャッフルする
		Collections.shuffle(randomList);

		// リストを返す
		return randomList;
	}

	// 漢字であるか否か判断
	private boolean isKanji(String txt) {
		if (StringUtils.isAsciiPrintable(txt)) {
			// ASCIIはfalse
			return false;
		}

		{
			Pattern p = Pattern.compile("^[ぁ-んァ-ヶーｱ-ﾝﾞﾟ]*$");
			Matcher m = p.matcher(txt);
			if (m.find()) {
				// ひらがな・全角カタカナ・半角カタカナはfalse
				return false;
			}
		}

		return true;
	}

}
