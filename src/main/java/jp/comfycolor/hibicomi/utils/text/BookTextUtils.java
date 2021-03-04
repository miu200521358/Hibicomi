package jp.comfycolor.hibicomi.utils.text;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class BookTextUtils {

	/**
	 * 表記バラバラのジャンルを揃える
	 *
	 * @param genreMap
	 * @param original
	 * @return
	 */
	public static String formatGenre(String txt, Map<String, List<String>> genreMap) {

		// カッコ以降は取り除く
		String original = StringUtils.substringBefore(txt, "(");

		for (Entry<String, List<String>> entrySet : genreMap.entrySet()) {
			for (String entryValue : entrySet.getValue()) {
				if (StringUtils.equals(original, entryValue)) {
					return entrySet.getKey();
				}
			}
		}

		// どうにも整理できなければオリジナルを返す
		return original;
	}

	/**
	 * 作品内容から、注意文っぽいのを除く
	 *
	 * @param original
	 * @return
	 */
	public static String formatDescription(String original) {
		String txt = original;

		txt = StringUtils.removeEnd(txt, "元に戻す");
		txt = StringUtils.remove(txt, "（※各巻のページ数は、表紙と奥付を含め片面で数えています）");
		txt = StringUtils.remove(txt, "※このコンテンツは必ずご購入前に閲覧可能かサンプルでお試し下さい。");
		txt = StringUtils.remove(txt, "サンプルを閲覧できない場合は、大変申し訳ありませんがご購入をお控え下さい。");
		txt = StringUtils.remove(txt, "※こちらの作品には音声は含まれておりません。予めご了承ください。");
		txt = StringUtils.remove(txt, "◆「コミックシアター」とは、デジタルならではの演出を付加することにより、従来の漫画を動画コンテンツとして進化させた新感覚の次世代コミックです。");
		txt = StringUtils.remove(txt, "重複購入にご注意ください");
		txt = StringUtils.remove(txt, "重複購入にお気を付けください");
		txt = StringUtils.remove(txt, "収録された作品が収められています");
		txt = StringUtils.remove(txt, "無料期間");
		txt = StringUtils.remove(txt, "無料試し読み閲覧期間");
		txt = StringUtils.remove(txt, "期間限定無料");
		txt = StringUtils.remove(txt, "続きをお楽しみいただくには、通常版（有料）をご利用ください。");
		txt = StringUtils.remove(txt, "期間限定");
		txt = StringUtils.remove(txt, "無料");
		txt = StringUtils.remove(txt, "以降はご利用できなくなります。");
		txt = StringUtils.remove(txt, "お試し版");
		txt = StringUtils.remove(txt, "試し読み");
		txt = StringUtils.remove(txt, "増量版");
		txt = StringUtils.remove(txt, "増量中");

		// 改行や空白を除去して返す
		return StringUtils.trimToEmpty(txt);
	}

	private static final String ALPHABET_PATTERN_STR = "a-zA-Z0-9";

	private static final String FORMATTED_PATTERN_STR = "(版|カラー|単話|限定|特典|電子単行本|短編)";

	private static final String FORMATTED_PARTS_PATTERN_STR = "(付き|付|入り|入)";

	private static final String FORMATTED_ONE_PATTERN_STR = "(短編|セット売り|全巻セット|バラ売り|タテコミ|ゲームブック|上(巻)?|下(巻)?)";

	private static final Pattern SPACE_PATTERN = Pattern.compile("(\\s|　)[^(\\s|　)]*(版|カラー)(\\s|　)?$");

	private static final String[][] SIGN_PATTERNS = {
			{ "【", "】" }, { "［", "］" }, { "［", "]" }, { "（", "）" }, { "＜", "＞" }, { "〔", "〕" }, { "\\(", "\\)" },
			{ "\\[", "\\]" }
	};

	private static final Pattern NUMBERS_PATTERN = Pattern.compile(
			"((\\s|　)|(＜|【|［|\\(|（)|(】|］|）|\\)|＞))((第)?([0-9０-９一二三四五六七八九十]+|上|下))(話|巻)?((】|］|）|\\)|＞)|(＜|【|［|\\(|（)|(\\s|　)|$)");
	private static final Pattern NUMBERS_PATTERN_ZENKAKU = Pattern.compile("[０-９]+");
	private static final Pattern NUMBERS_PATTERN_KANJI = Pattern.compile("[一二三四五六七八九十]+");

	private static final String[][] SYMBOL_PATTERNS = {
			{ "!", "！" }
			//		, {""", "＂"}
			, { "#", "＃" }, { "&", "＆" }, { "(", "（" }, { ")", "）" }, { "+", "＋" }, { ",", "，" }, { "-", "－" },
			{ "･", "・" }, { ";", "；" }, { "<", "＜" }, { "=", "＝" }, { ">", "＞" }, { "?", "？" }, { "@", "＠" },
			{ "[", "［" }
			//		, {"\", "＼"}
			, { "]", "］" }, { "^", "＾" }, { "_", "＿" }, { "`", "｀" }, { "{", "｛" }, { "|", "｜" }, { "}", "｝" },
			{ "｢", "「" }, { "｣", "」" }, { "~", "～" }, { "〜", "～" }, { "　", " " } // 空白は半角に
			, { "-", "－" }, { "—", "－" }, { "｡", "。" }, { "､", "、" }, { "・・・", "…" }
	};

	private static final String[][] SYMBOL_PREFIX_PATTERNS = {
			{ "'", "’" }, { ".", "．" }, { ":", "：" }, { "/", "／" }, { "*", "＊" }, { "$", "＄" }, { "%", "％" }
	};

	/**
	 * タイトルのフォーマット
	 *
	 * @param original
	 * @return
	 */
	public static String formatTitle(String original) {
		String txt = original;

		// 複数回回す
		txt = formatTitleInner(txt);
		txt = formatTitleInner(txt);
		txt = formatTitleInner(txt);
		txt = formatTitleInner(txt);
		txt = formatTitleInner(txt);
		txt = formatTitleInner(txt);

		return txt;
	}

	public static String formatTitleNumber(String original) {
		//巻数チェック
		Matcher m = NUMBERS_PATTERN.matcher(original);
		if (m.find()) {
			// 巻数字を取得
			String numberStr = m.group(7);

			if (NUMBERS_PATTERN_ZENKAKU.matcher(numberStr).find()) {
				// 半角数字
				return changeNumFullToHalf(numberStr);
			}
			else if (NUMBERS_PATTERN_KANJI.matcher(numberStr).find()) {
				// 半角数字
				return convertPositiveKanjiNumber(numberStr);
			}

			// 上記以外はそのまま返す
			return numberStr;
		}

		return null;
	}

	/**
	 * <p>[概 要] 全角数字⇒半角数字への変換</p>
	 * <p>[詳 細] </p>
	 * <p>[備 考] </p>
	 * @param  str 変換対象文字列
	 * @return 変換後文字列
	 */
	private static String changeNumFullToHalf(String str) {
		String result = null;
		if (str != null) {
			StringBuilder sb = new StringBuilder(str);
			for (int i = 0; i < sb.length(); i++) {
				int c = (int) sb.charAt(i);
				if (c >= 0xFF10 && c <= 0xFF19) {
					sb.setCharAt(i, (char) (c - 0xFEE0));
				}
			}
			result = sb.toString();
		}
		return result;
	}

	/**
	 * 漢数字をアラビア数字の文字列に変換
	 *
	 * @param targetValue
	 * @return
	 */
	private static String convertPositiveKanjiNumber(String targetValue) {
		if (StringUtils.isEmpty(targetValue)) {
			return null;
		}

		if ("零".equals(targetValue)) {
			return "0";
		}

		int firstDegit = 1;
		int fourthDegit = 0;
		int total = 0;
		for (int i = 0; i < targetValue.length(); i++) {
			char kanjiNumber = targetValue.charAt(i);
			switch (kanjiNumber) {
			case '一':
				firstDegit = 1;
				break;
			case '二':
				firstDegit = 2;
				break;
			case '三':
				firstDegit = 3;
				break;
			case '四':
				firstDegit = 4;
				break;
			case '五':
				firstDegit = 5;
				break;
			case '六':
				firstDegit = 6;
				break;
			case '七':
				firstDegit = 7;
				break;
			case '八':
				firstDegit = 8;
				break;
			case '九':
				firstDegit = 9;
				break;
			case '十':
				fourthDegit += (firstDegit != 0 ? firstDegit : 1) * 10;
				firstDegit = 0;
				break;
			case '百':
				fourthDegit += (firstDegit != 0 ? firstDegit : 1) * 100;
				firstDegit = 0;
				break;
			case '千':
				fourthDegit += (firstDegit != 0 ? firstDegit : 1) * 1_000;
				firstDegit = 0;
				break;
			case '万':
				fourthDegit += firstDegit;
				total += (fourthDegit != 0 ? fourthDegit : 1) * 10_000;
				fourthDegit = 0;
				firstDegit = 0;
				break;
			case '億':
				fourthDegit += firstDegit;
				total += (fourthDegit != 0 ? fourthDegit : 1) * 100_000_000;
				fourthDegit = 0;
				firstDegit = 0;
				break;
			default:
				firstDegit = 0;
				break;
			}
		}
		return Integer.toString(total + fourthDegit + firstDegit);
	}

	private static String formatTitleInner(String original) {
		String txt = original;

		{
			//巻数除去
			Matcher m = NUMBERS_PATTERN.matcher(txt);
			if (m.find()) {
				// 巻数字以降を除去
				txt = StringUtils.left(txt, m.start(5));
			}
		}
		{
			//括弧でそれっぽいの除去1
			for (String[] signs : SIGN_PATTERNS) {
				txt = formatTitlePattern(txt, signs);
			}
		}
		{
			//空白でそれっぽいの除去
			Matcher m = SPACE_PATTERN.matcher(txt);
			txt = m.replaceAll(" ");
		}
		{
			//括弧でそれっぽいの除去(ピン系)
			Pattern p = Pattern.compile("(\\s|　)" + FORMATTED_ONE_PATTERN_STR + "\\s*$");
			Matcher m = p.matcher(txt);
			txt = m.replaceAll(" ");
		}
		{
			// 全角英数字を半角に変換
			txt = zenkakuToHankaku(txt);
		}

		// 余計なのまで正規化しちゃうので、使わない 1/23
		//		{
		//			// normalizer
		//			txt = Normalizer.normalize(txt, Form.NFKC);
		//		}

		// 表記揺れの整備
		for (String[] symbols : SYMBOL_PATTERNS) {
			txt = StringUtils.replace(txt, symbols[0], symbols[1]);
		}

		//		logger.debug("SYMBOL_PATTERNS: "+ txt);

		for (String[] symbols : SYMBOL_PREFIX_PATTERNS) {
			// prefixにより切り替えるのはこっちで判定

			boolean isZen = true;

			// 半角判定 ----
			{
				// 半角英数＋半角記号
				Pattern hp1 = Pattern.compile("[" + ALPHABET_PATTERN_STR + "]+\\" + symbols[0]);
				Matcher hm1 = hp1.matcher(txt);
				// 半角記号＋半角英数
				Pattern hp2 = Pattern.compile("\\" + symbols[0] + "[" + ALPHABET_PATTERN_STR + "]+");
				Matcher hm2 = hp2.matcher(txt);

				if (!hm1.find() && !hm2.find()) {
					// 半角に続かない場合
					//					logger.debug("半角に続かない場合");

					// 半角英数＋全角記号
					Pattern zp1 = Pattern.compile(ALPHABET_PATTERN_STR + "+" + symbols[1]);
					Matcher zm1 = zp1.matcher(txt);
					if (zm1.find()) {
						//						logger.debug("半角英数＋全角記号");
						txt = StringUtils.replace(txt, symbols[1], symbols[0]);
						isZen = false;
					} else {
						// 全角記号＋半角英数
						Pattern zp2 = Pattern.compile(symbols[1] + ALPHABET_PATTERN_STR + "+");
						Matcher zm2 = zp2.matcher(txt);
						if (zm2.find()) {
							// 半角に続く記号の場合、半角
							//							logger.debug("全角記号＋半角英数");
							txt = StringUtils.replace(txt, symbols[1], symbols[0]);
							isZen = false;
						}
					}
				} else {
					isZen = false;
				}
			}

			// 全角判定 ----
			if (isZen) {
				// 全角英数＋全角記号
				Pattern zp1 = Pattern.compile("[^" + ALPHABET_PATTERN_STR + "]+" + symbols[1]);
				Matcher zm1 = zp1.matcher(txt);
				// 全角記号＋全角英数
				Pattern zp2 = Pattern.compile(symbols[1] + "[^" + ALPHABET_PATTERN_STR + "]+");
				Matcher zm2 = zp2.matcher(txt);

				if (!zm1.find() && !zm2.find()) {
					// 全角に続かない場合
					//					logger.debug("全角に続かない場合");

					// 全角英数＋半角記号
					Pattern hp1 = Pattern.compile("[^" + ALPHABET_PATTERN_STR + "]+\\" + symbols[0]);
					Matcher hm1 = hp1.matcher(txt);
					if (hm1.find()) {
						//						logger.debug("全角英数＋半角記号");
						txt = StringUtils.replace(txt, symbols[0], symbols[1]);
					} else {
						// 半角記号＋全角英数
						Pattern hp2 = Pattern.compile("\\" + symbols[0] + "[^" + ALPHABET_PATTERN_STR + "]+");
						Matcher hm2 = hp2.matcher(txt);
						if (hm2.find()) {
							//							logger.debug("半角記号＋全角英数");
							// 半角に続く記号の場合、半角
							txt = StringUtils.replace(txt, symbols[0], symbols[1]);
						}
					}
				}
			}
		}

		//		logger.debug("SYMBOL_PREFIX_PATTERNS: "+ txt);

		{
			// trim
			txt = StringUtils.trim(txt);
		}

		// 全部除去されてしまったら、代わりにもう一度タイトル設定
		if (StringUtils.isEmpty(txt)) {
			//	    	logger.debug("txt is empty");
			txt = original;
		}

		return txt;
	}

	private static String formatTitlePattern(String txt, String[] signs) {
		{
			//括弧でそれっぽいの除去(ピン系)
			Pattern p = Pattern.compile(signs[0] + FORMATTED_ONE_PATTERN_STR + signs[1]);
			Matcher m = p.matcher(txt);
			txt = m.replaceAll(" ");
		}
		{
			//括弧でそれっぽいの除去1
			Pattern p = Pattern.compile("(\\s|　)?(" + signs[0] + ")[^" + signs[0] + "]*" + FORMATTED_PATTERN_STR + "[^"
					+ signs[1] + "]*(" + signs[1] + ")(\\.{3})?(\\s|　)?");
			Matcher m = p.matcher(txt);
			txt = m.replaceAll(" ");
		}
		{
			//括弧でそれっぽいの除去1
			Pattern p = Pattern.compile("(\\s|　)?(" + signs[0] + ")[^" + signs[0] + "]*" + FORMATTED_PARTS_PATTERN_STR
					+ "[^" + signs[1] + "]*(" + signs[1] + ")(\\.{3})?(\\s|　)?$");
			Matcher m = p.matcher(txt);
			txt = m.replaceAll(" ");
		}
		{
			//括弧でそれっぽいの除去1
			Pattern p = Pattern
					.compile("(\\s|　)?(" + signs[0] + ")[^" + signs[0] + "]*[^" + signs[1] + "]*(\\.{3})(\\s|　)?");
			Matcher m = p.matcher(txt);
			txt = m.replaceAll(" ");
		}
		{
			// 始まり括弧だけのがあったら、除去
			Pattern p = Pattern.compile(signs[0] + "$");
			Matcher m = p.matcher(txt);
			txt = m.replaceAll(" ");
		}

		return txt;
	}

	/**
	 * 全角英数字を半角に変換
	 * @param value
	 * @return
	 */
	private static String zenkakuToHankaku(String value) {
		StringBuilder sb = new StringBuilder(value);
		for (int i = 0; i < sb.length(); i++) {
			int c = (int) sb.charAt(i);
			if ((c >= 0xFF10 && c <= 0xFF19) || (c >= 0xFF21 && c <= 0xFF3A) || (c >= 0xFF41 && c <= 0xFF5A)) {
				sb.setCharAt(i, (char) (c - 0xFEE0));
			}
		}
		value = sb.toString();
		return value;
	}

}
