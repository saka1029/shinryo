package saka1029.shinryo.parser;

import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 文字定数としての正規表現を集めたクラスです。 正規表現を組み合わせて新たな正規表現を作る関数もあります。
 */
public class Pat {

    static final Logger LOGGER = Logger.getLogger(Pat.class.getName());

    private Pat() {
    }

    public static final String パス区切り = "_";
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
    public static final String カナ = "[" + アイウ + "へ]{1,2}"; // ひらがなの「へ」を追加。２桁対応。
    public static final String 括弧カナ = paren(カナ);
    public static final String 漢字数字 = "一二三四五六七八九十";
    public static final String 漢数字 = "[" + 漢字数字 + "]+";
    public static final String 漢数字の = repeat(漢数字, "の", 漢数字);
    public static final String 括弧漢数字 = paren(漢数字);
    public static final String 区分番号 = repeat("[A-ZＡ-Ｚ][0-9０-９]{3}", "[ー－―‐-]", 数字);
//    public static final String 区分大分類 = "＜(?!通則).*＞";
    public static final String 区分分類 = 左括弧 + "[^0-9０-９)）][^0-9)）]*" + 右括弧;
    public static final String 調剤告示区分番号 = repeat("[０-９]{2}", "の", 数字);
    public static final String 調剤通知区分番号 = repeat("区分[０-９]{2}", "の", 数字);
    public static final String 丸数 = "①②③④⑤⑥⑦⑧⑨⑩"
        + "⑪⑫⑬⑭⑮⑯⑰⑱⑲⑳"
        + "㉑㉒㉓㉔㉕㉖㉗㉘㉙㉚"
        + "㉛㉜㉝㉞㉟㊱㊲㊳㊴㊵"
        + "㊶㊷㊸㊹㊺㊻㊼㊽㊾㊿";
    public static final String 丸数字 = "[" + 丸数 + "]";
    public static final String 例 = "例" + 数字; 
    public static final String 様式名パターン = repeat("別[紙添]様式\\s*" + 数字, "\\s*[のー－―‐-]\\s*", 数字);
    public static final Pattern 医科リンクパターン = Pattern.compile("(?<K>" + 区分番号 + ")|(?<Y>" + 様式名パターン + ")");
    public static final Pattern 調剤リンクパターン = Pattern.compile("(?<K>" + 調剤告示区分番号 + ")|(?<Y>" + 様式名パターン + ")");
    public static final String リンク = "<a href='%s.%s'>%s</a>";
    public static String リンク(Pattern pattern, String s) {
        StringBuilder sb = new StringBuilder();
        Matcher m = pattern.matcher(s);
        while (m.find()) {
            String link = m.group();
            if (m.group("K") != null)
                m.appendReplacement(sb, リンク.formatted(Pat.正規化(link), "html", link));
            else if (m.group("Y") != null)
                m.appendReplacement(sb, リンク.formatted("pdf/" + Pat.正規化(link), "pdf", link));
        }
        m.appendTail(sb);
        return sb.toString();
    }
    public static final Function<String, String> 医科リンク = s -> リンク(医科リンクパターン, s);
    public static final Function<String, String> 調剤リンク = s -> リンク(調剤リンクパターン, s);

    public static final Function<String, String> 数字id = s -> 正規化(s);
    public static final Function<String, String> アイウid = s -> カナ正規化(アイウ, s);
    public static final Function<String, String> イロハid = s -> カナ正規化(イロハ, s);
    public static final Function<String, String> 丸数字id = s -> 正規化(s);
    public static final Function<String, String> 区分番号id = s -> 正規化(s);
    public static final Function<String, String> 漢数字id = s -> 漢数字正規化(s);

    public static Function<String, String> 固定値id(String 固定値) {
        return s -> 固定値;
    }

    public static String 正規化(String s) {
        s = s.replaceAll("\\s", "");
        s = s.replaceAll("[()（）]|まで|区分|別表|第|部|章|節|款|例", "");
        s = s.replaceAll("[のー－―‐-]", "-");
        s = s.replaceAll("へ", "ヘ"); // ひらがなの「へ」をカタカナの「ヘ」に変換する。
        s = s.replaceAll("から|及び|、", "x");
        s = s.replaceAll("別添", "T").replaceAll("別紙", "S").replaceAll("様式", "Y");
        return Normalizer.normalize(s, Form.NFKC);
    }

    public static String 漢数字正規化(String s) {
        s = 正規化(s);
        s = s.replaceAll("(^|[+-_x])十([+-_x])", "$110$2");
        s = s.replaceAll("(^|[+-_x])十$", "$110");
        s = s.replaceAll("(^|[+-_x])十", "$11");
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

    /**
     * <pre>
     * アイウの場合:
     * ア   → 1
     * イ   → 2
     * ウ   → 3
     *   .....
     * ン   → 48
     * アア → 49
     * アイ → 50
     * 
     * イロハの場合:
     * イ   → 1
     * ロ   → 2
     * ハ   → 3
     *   .....
     * ン   → 48
     * イイ → 49
     * イロ → 50
     * </pre>
     */
    public static String カナ正規化(String set, String s) {
        s = 正規化(s);
        int base = set.length(), n = 0;
        for (int i = 0, size = s.length(); i < size; ++i) {
            int index = set.indexOf(s.charAt(i));
            if (index < 0)
                throw new RuntimeException("「" + s + "」は「" + set.substring(0, 3) + "」の中にない文字を含みます");
            n = n * base + index + 1;
        }
        return Integer.toString(n);
    }

    static final Pattern 数字列 = Pattern.compile("\\d+");

    /**
     * 区分番号の順序を比較するための正規化
     * 単純に比較すると"B001-2" > "B001-12"であるが、
     * "B001-2" < "B001-12"とするため、
     * それぞれ"B00001-00002"および"B00001-00012"に変換する。
     */
    public static String 区分順序化(String kubun) {
        return 数字列.matcher(正規化(kubun)).replaceAll(m -> {
            String n = m.group();
            return "0".repeat(5 - n.length()) + n;
        });
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
        return "(" + body + ")(" + conj + "(" + body2 + "))*";
    }

    public static String fromTo(String body) {
        return "(" + body + ")(から(" + body + ")まで|及び(" + body + ")|、(" + body + "))?";
    }

}
