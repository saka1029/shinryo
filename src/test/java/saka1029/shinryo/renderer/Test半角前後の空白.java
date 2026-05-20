package saka1029.shinryo.renderer;

import static org.junit.Assert.assertEquals;

import java.util.regex.Pattern;

import org.junit.Test;

public class Test半角前後の空白 {

    static Pattern 半角全角間空白 = Pattern.compile(
            "(?<=[^\u0001-\u007E])\\s+(?=[\\dA-Za-z])"
            + "|(?<=[\\dA-Za-z])\\s+(?=[^\u0001-\u007E])");

    static String 半角全角間空白削除(String s) {
        return 半角全角間空白.matcher(s).replaceAll("");
    }

    @Test
    public void test半角前後の空白() {
        assertEquals("あいう20えお", 半角全角間空白削除("あいう  20  えお"));
        assertEquals("abc 20 days", 半角全角間空白削除("abc 20 days"));
        assertEquals("It is like a finger", 半角全角間空白削除("It is like a finger"));
    }

    @Test
    public void testA000() {
        String s = """
            (14) 患者が任意に診療を中止し、１月以上経過した後、再び同一の保険医療機関において診
            療を受ける場合には、その診療が同一病名又は同一症状によるものであっても、その際の診療は、
            初診として取り扱う。なお、この場合において、１月の期間の計算は、
            例えば、２月 10 日～３月９日、９月 15 日～10月 14 日等と計算する。
            """;
        String e = """
            (14) 患者が任意に診療を中止し、１月以上経過した後、再び同一の保険医療機関において診
            療を受ける場合には、その診療が同一病名又は同一症状によるものであっても、その際の診療は、
            初診として取り扱う。なお、この場合において、１月の期間の計算は、
            例えば、２月10日～３月９日、９月15日～10月14日等と計算する。
            """;
        assertEquals(e, 半角全角間空白削除(s));
    }

}
