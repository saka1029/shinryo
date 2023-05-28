package saka1029.shinryo.parser;

public class Pat {

    public static final String 数 = "[0-9０-９]";
    public static final String 数字 = 数 + "+";
    public static final String 数字の = repeat(数字, "の");
    public static final String 左括弧 = "[(（]";
    public static final String 右括弧 = "[)）]";
    public static final String 括弧数字 = paren(数字);
    public static final String アイウ = "アイウエオカキクケコ"
	    + "サシスセソタチツテト"
	    + "ナニヌネノハヒフヘホ"
	    + "マミムメモヤユヨ"
	    + "ラリルレロワヰヱヲン";
    public static final String カナ = "[" + アイウ + "]";
    public static final String 括弧カナ = paren(カナ);
    public static final String 漢字数字 = "一二三四五六七八九十";
    public static final String 漢数字 = "[" + 漢字数字 + "]+";
    public static final String 漢数字の = repeat(漢数字, "の");
    public static final String 括弧漢数字 = paren(漢数字);

    public static String numberHeader(String number) {
        return "\\s*(?<N>" + number + ")\\s+(?<H>.*)\\s*";
    }

    public static String number(String number) {
        return "\\s*(?<N>" + number + ")(?<H>)\\s*";
    }
    
    public static String paren(String body) {
        return 左括弧 + "(" + body + ")" + 右括弧;
    }

    public static String repeat(String body, String conj) {
        return repeat(body, conj, body);
    }
    
    public static String repeat(String body, String conj, String body2) {
        return "(" + body + ")("  + conj + "(" + body2 + "))*";
    }
    
    public static String fromTo(String body) {
        return "(" + body + ")(から(" + body + ")まで|及び(" + body + "))?";
    }

}
