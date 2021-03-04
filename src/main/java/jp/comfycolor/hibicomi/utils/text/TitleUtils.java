package jp.comfycolor.hibicomi.utils.text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class TitleUtils {

	public static String formatDescription(String original) {
		String txt = original;

		// Renta!
		txt = StringUtils.removeEnd(txt, "元に戻す");
		txt = StringUtils.remove(txt, "（※各巻のページ数は、表紙と奥付を含め片面で数えています）");
		txt = StringUtils.remove(txt, "重複購入にご注意ください");


		// 改行や空白を除去して返す
		return StringUtils.trimToEmpty(txt);
	}

	private static final String ALPHABET_PATTERN_STR = "a-zA-Z0-9";

	private static final String FORMATTED_PATTERN_STR = "(版|カラー|単話|限定|特典|電子単行本|短編)";

	private static final String FORMATTED_PARTS_PATTERN_STR = "(付き|付|入り|入)";

	private static final String FORMATTED_ONE_PATTERN_STR = "(短編|セット売り|全巻セット|バラ売り|タテコミ|ゲームブック|上(巻)?|下(巻)?)";

    private static final Pattern SPACE_PATTERN = Pattern.compile("(\\s|　)[^(\\s|　)]*(版|カラー)(\\s|　)?$");

	private static final String[][] SIGN_PATTERNS = {
			{"【", "】"}
			, {"［", "］"}
			, {"［", "]"}
			, {"（", "）"}
			, {"＜", "＞"}
			, {"〔", "〕"}
			, {"\\(", "\\)"}
			, {"\\[", "\\]"}
	};

    private static final Pattern NUMBERS_PATTERN = Pattern.compile("((\\s|　)|(＜|【|［|\\(|（)|(】|］|）|\\)|＞))((第)?([0-9０-９]+|上|下))(話|巻)?((】|］|）|\\)|＞)|(＜|【|［|\\(|（)|(\\s|　)|$)");

    private static final String[][] SYMBOL_PATTERNS = {
		{"!", "！"}
//		, {""", "＂"}
		, {"#", "＃"}
		, {"&", "＆"}
		, {"(", "（"}
		, {")", "）"}
		, {"+", "＋"}
		, {",", "，"}
		, {"-", "－"}
		, {"･", "・"}
		, {";", "；"}
		, {"<", "＜"}
		, {"=", "＝"}
		, {">", "＞"}
		, {"?", "？"}
		, {"@", "＠"}
		, {"[", "［"}
//		, {"\", "＼"}
		, {"]", "］"}
		, {"^", "＾"}
		, {"_", "＿"}
		, {"`", "｀"}
		, {"{", "｛"}
		, {"|", "｜"}
		, {"}", "｝"}
		, {"｢", "「"}
		, {"｣", "」"}
		, {"~", "～"}
		, {"〜", "～"}
		, {"　", " "}	// 空白は半角に
		, {"-", "－"}
		, {"—", "－"}
		, {"｡", "。"}
		, {"､", "、"}
		, {"・・・", "…"}
    };

    private static final String[][] SYMBOL_PREFIX_PATTERNS = {
		{"'", "’"}
		, {".", "．"}
		, {":", "："}
		, {"/", "／"}
		, {"*", "＊"}
		, {"$", "＄"}
		, {"%", "％"}
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
	    	return m.group(7);
	    }

	    return null;
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
		    Pattern p = Pattern.compile("(\\s|　)"+ FORMATTED_ONE_PATTERN_STR + "\\s*$");
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
				Pattern hp1 = Pattern.compile("["+ ALPHABET_PATTERN_STR +"]+\\"+ symbols[0]);
				Matcher hm1 = hp1.matcher(txt);
				// 半角記号＋半角英数
				Pattern hp2 = Pattern.compile("\\"+ symbols[0] + "["+ ALPHABET_PATTERN_STR +"]+");
				Matcher hm2 = hp2.matcher(txt);

				if (!hm1.find() && !hm2.find()) {
					// 半角に続かない場合
//					logger.debug("半角に続かない場合");

					// 半角英数＋全角記号
					Pattern zp1 = Pattern.compile(ALPHABET_PATTERN_STR + "+"+ symbols[1]);
					Matcher zm1 = zp1.matcher(txt);
					if (zm1.find()) {
//						logger.debug("半角英数＋全角記号");
						txt = StringUtils.replace(txt, symbols[1], symbols[0]);
						isZen = false;
					}
					else {
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
				}
				else {
					isZen = false;
				}
			}

			// 全角判定 ----
			if (isZen) {
				// 全角英数＋全角記号
				Pattern zp1 = Pattern.compile("[^"+ ALPHABET_PATTERN_STR +"]+"+ symbols[1]);
				Matcher zm1 = zp1.matcher(txt);
				// 全角記号＋全角英数
				Pattern zp2 = Pattern.compile( symbols[1] + "[^"+ ALPHABET_PATTERN_STR +"]+");
				Matcher zm2 = zp2.matcher(txt);

				if (!zm1.find() && !zm2.find()) {
					// 全角に続かない場合
//					logger.debug("全角に続かない場合");

					// 全角英数＋半角記号
					Pattern hp1 = Pattern.compile("[^"+ ALPHABET_PATTERN_STR +"]+\\"+ symbols[0]);
					Matcher hm1 = hp1.matcher(txt);
					if (hm1.find()) {
//						logger.debug("全角英数＋半角記号");
						txt = StringUtils.replace(txt, symbols[0], symbols[1]);
					}
					else {
						// 半角記号＋全角英数
						Pattern hp2 = Pattern.compile("\\"+ symbols[0] + "[^"+ ALPHABET_PATTERN_STR +"]+");
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
		    Pattern p = Pattern.compile("(\\s|　)?("+ signs[0] +")[^"+ signs[0] +"]*"+ FORMATTED_PATTERN_STR +"[^"+ signs[1] +"]*("+ signs[1] +")(\\.{3})?(\\s|　)?");
		    Matcher m = p.matcher(txt);
		    txt = m.replaceAll(" ");
		}
		{
			//括弧でそれっぽいの除去1
		    Pattern p = Pattern.compile("(\\s|　)?("+ signs[0] +")[^"+ signs[0] +"]*"+ FORMATTED_PARTS_PATTERN_STR +"[^"+ signs[1] +"]*("+ signs[1] +")(\\.{3})?(\\s|　)?$");
		    Matcher m = p.matcher(txt);
		    txt = m.replaceAll(" ");
		}
		{
			//括弧でそれっぽいの除去1
		    Pattern p = Pattern.compile("(\\s|　)?("+ signs[0] +")[^"+ signs[0] +"]*[^"+ signs[1] +"]*(\\.{3})(\\s|　)?");
		    Matcher m = p.matcher(txt);
		    txt = m.replaceAll(" ");
		}
		{
			// 始まり括弧だけのがあったら、除去
		    Pattern p = Pattern.compile(signs[0] +"$");
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
