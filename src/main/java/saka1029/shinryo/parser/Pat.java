package saka1029.shinryo.parser;

public class Pat {

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
	    + "ナニヌネノハヒフヘホ"
	    + "マミムメモヤユヨ"
	    + "ラリルレロワヰヱヲン"
	    + "へ"; // ひらがなの「へ」調剤通知区分１０の３
    public static final String カナ = "[" + アイウ + "]";
    public static final String 括弧カナ = paren(カナ);
    public static final String 漢字数字 = "一二三四五六七八九十";
    public static final String 漢数字 = "[" + 漢字数字 + "]+";
    public static final String 漢数字の = repeat(漢数字, "の", 漢数字);
    public static final String 括弧漢数字 = paren(漢数字);
    public static final String 区分番号 = repeat("[A-ZＡ-Ｚ][0-9０-９]{3}", "[-ー－‐]", 数字);
    public static final String 調剤告示区分番号 = repeat("[０-９]{2}", "の", 数字);
    public static final String 調剤通知区分番号 = repeat("区分[０-９]{2}", "の", 数字);
    public static final String 丸数 = "①②③④⑤⑥⑦⑧⑨⑩"
    		+ "⑪⑫⑬⑭⑮⑯⑰⑱⑲⑳"
    		+ "㉑㉒㉓㉔㉕㉖㉗㉘㉙㉚"
    		+ "㉛㉜㉝㉞㉟㊱㊲㊳㊴㊵"
    		+ "㊶㊷㊸㊹㊺㊻㊼㊽㊾㊿";
    public static final String 丸数字 = "[" + 丸数 + "]";

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