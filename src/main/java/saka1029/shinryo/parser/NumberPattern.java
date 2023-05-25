package saka1029.shinryo.parser;

import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class NumberPattern {

	public static final String normalize(String s) {
			String result = Normalizer.normalize(s, Form.NFKC);
			result = result.replaceAll("―", "-");
			return result;
	}

	/**
	 * 漢数字表現をアラビア数字表現に変換します。
	 * 1または2桁の数字しか処理できません。
	 */
	public static String translate漢数字(String s) {
		return s.replaceFirst("^十$", "10").replaceFirst("^十", "1").replaceAll("十", "")
				.replaceAll("一", "1").replaceAll("二", "2").replaceAll("三", "3")
				.replaceAll("四", "4").replaceAll("五", "5").replaceAll("六", "6")
				.replaceAll("七", "7").replaceAll("八", "8").replaceAll("九", "9");
	}

	public static final NumberPattern 数字 = new NumberPattern("", "[0-9０-９]+", "") {
		@Override
		public String translate(String s) {
			return normalize(s);
		}
	};

	public static final NumberPattern 括弧数字 = new NumberPattern("[(（]", "[0-9０-９]+", "[)）]") {
		@Override
		public String translate(String s) {
			return Normalizer.normalize(s, Form.NFKC);
		}
	};

	public static final NumberPattern 漢数字 = new NumberPattern("", "[一二三四五六七八九十]+", "") {
		@Override
		public String translate(String s) {
			return translate漢数字(s);
		}
	};

	public static final NumberPattern 括弧漢数字 = new NumberPattern("[(（]", "[一二三四五六七八九十]+", "[)）]") {
		@Override
		public String translate(String s) {
			return translate漢数字(s);
		}
	};
	
	public static final NumberPattern 区分番号 = new NumberPattern("", "[A-ZＡ-Ｚ][0-9０-９]{3}([-－ー―‐][0-9０-９]+)*", "") {
		@Override
		public String translate(String s) {
			return normalize(s);
		}
	};

	public final String prefix, body, suffix, full;
	public final Pattern pattern;

	NumberPattern(String prefix, String body, String suffix) {
		this.prefix = prefix;
		this.body = body;
		this.suffix = suffix;
		this.full = prefix + body + suffix;
		this.pattern = Pattern.compile(body);
	}

	public abstract String translate(String s);

	public String id(String number) {
		Matcher m = pattern.matcher(number);
		if (!m.find())
			throw new RuntimeException(number + "に" + body + "がありません");
		return translate(m.group());
	}

}
