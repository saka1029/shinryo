package saka1029.shinryo.parser;

import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.function.Function;
import java.util.logging.Logger;

public class Pat {

    static final Logger logger = Logger.getLogger(Pat.class.getName());

	private Pat() {
	}

    public static final String 数 = "[0-9０-９]";
    public static final String 数字 = 数 + "+";
    public static final String 数字の = repeat(数字, "の", 数字);
    public static final String 左括弧 = "[(（]";
    public static final String 右括弧 = "[)）]";
    public static final String 括弧数字 = paren(数字);
    public static final String アイウ = "アイウエオカキクケコ"
	    + "サシスセソタチツテト"
	    + "ナニヌネノハヒフヘホ" // ひらがなの「へ」調剤通知区分１０の３
	    + "マミムメモヤユヨ"
	    + "ラリルレロワヰヱヲン";
    public static final String イロハ = "イロハニホヘトチリヌルヲ"
        + "ワカヨタレソツネナラム"
        + "ウヰノオクヤマケフコエテ"
        + "アサキユメミシヱヒモセスン";
    public static final String カナ = "[" + アイウ + "へ]";  // ひらがなの「へ」を追加。
    public static final String 括弧カナ = paren(カナ);
    public static final String 漢字数字 = "一二三四五六七八九十";
    public static final String 漢数字 = "[" + 漢字数字 + "]+";
    public static final String 漢数字の = repeat(漢数字, "の", 漢数字);
    public static final String 括弧漢数字 = paren(漢数字);
    public static final String 区分番号 = repeat("[A-ZＡ-Ｚ][0-9０-９]{3}", "[ー－‐-]", 数字);
    public static final String 調剤告示区分番号 = repeat("[０-９]{2}", "の", 数字);
    public static final String 調剤通知区分番号 = repeat("区分[０-９]{2}", "の", 数字);
    public static final String 丸数 = "①②③④⑤⑥⑦⑧⑨⑩"
    		+ "⑪⑫⑬⑭⑮⑯⑰⑱⑲⑳"
    		+ "㉑㉒㉓㉔㉕㉖㉗㉘㉙㉚"
    		+ "㉛㉜㉝㉞㉟㊱㊲㊳㊴㊵"
    		+ "㊶㊷㊸㊹㊺㊻㊼㊽㊾㊿";
    public static final String 丸数字 = "[" + 丸数 + "]";

    public static final Function<String, String> 数字id = s -> 正規化(s);
    public static final Function<String, String> アイウid = s -> indexOf(アイウ, 正規化(s));
    public static final Function<String, String> イロハid = s -> indexOf(イロハ, 正規化(s));
    public static final Function<String, String> 丸数字id = s -> 正規化(s);
    public static final Function<String, String> 区分番号id = s -> 正規化(s);
    public static final Function<String, String> 漢数字id = s -> 漢数字正規化(s);
    public static Function<String, String> 固定値id(String 固定値) {
        return s -> 固定値;
    }
        
    public static String 正規化(String s) {
        s = s.replaceAll("[()（）]|まで|区分|別表|第|章|節", "");
        s = s.replaceAll("[のー－‐-]", "-");
        s = s.replaceAll("へ", "ヘ"); // ひらがなの「へ」をカタカナの「ヘ」に変換する。
        s = s.replaceAll("から|及び", "+");
        return Normalizer.normalize(s, Form.NFKC);
    }
    
    public static String 漢数字正規化(String s) {
        s = 正規化(s);
        s = s.replaceAll("(^|[+-])十([+-])", "$110$2");
        s = s.replaceAll("(^|[+-])十$", "$110");
        s = s.replaceAll("(^|[+-])十", "$11");
        s = s.replaceAll("十", "");
        s = s.replaceAll("一", "1");
        s = s.replaceAll("二", "2");
        s = s.replaceAll("三", "3");
        s = s.replaceAll("四", "4");
        s = s.replaceAll("五", "5");
        s = s.replaceAll("六", "6");
        s = s.replaceAll("七", "7");
        s = s.replaceAll("八", "8");
        s = s.replaceAll("九", "9");
        return s;
    }
    
    public static String indexOf(String list, String s) {
        int index = list.indexOf(s);
        if (index < 0)
            throw new RuntimeException("文字「" + s + "」は「" + list + "」の中に見つかりません");
        return "" + (index + 1);
    }

    public static String numberHeader(String number) {
        return "\\s*(?<N>" + number + ")\\s+(?<H>.*)\\s*";
    }

    public static String number(String number) {
        return "\\s*(?<N>" + number + ")(?<H>)\\s*";
    }
    
    public static String paren(String body) {
        return 左括弧 + "(" + body + ")" + 右括弧;
    }
    
    public static String repeat(String body, String conj, String body2) {
        return "(" + body + ")("  + conj + "(" + body2 + "))*";
    }
    
    public static String fromTo(String body) {
        return "(" + body + ")(から(" + body + ")まで|及び(" + body + "))?";
    }

}
