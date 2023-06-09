package saka1029.shinryo.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

import saka1029.shinryo.common.Common;

public class TestPat {

    static final Logger logger = Common.logger(TestPat.class);

    @Test
    public void testのからまで() {
        String s = Pat.numberHeader(Pat.fromTo(Pat.repeat(Pat.数字, "の", Pat.数字)));
        Pattern pattern = Pattern.compile(s);
        Matcher m = pattern.matcher("１の２の３から１の３の４まで 削除");
//        logger.info(s);
        assertTrue(m.matches());
        assertEquals("１の２の３から１の３の４まで", m.group("N"));
        assertEquals("削除", m.group("H"));
    }

    @Test
    public void test別表第漢字() {
        String s = Pat.numberHeader(Pat.repeat("別表第" + Pat.漢数字, "の", Pat.漢数字));
        Pattern pattern = Pattern.compile(s);
        Matcher m = pattern.matcher("別表第十五の二の三 ある届出");
//        logger.info(s);
        assertTrue(m.matches());
        assertEquals("別表第十五の二の三", m.group("N"));
        assertEquals("ある届出", m.group("H"));
    }

    @Test
    public void test括弧漢数字() {
        String s = Pat.numberHeader(Pat.括弧漢数字);
        Pattern pattern = Pattern.compile(s);
        Matcher m = pattern.matcher("(十五) ある施設基準");
//        logger.info(s);
        assertTrue(m.matches());
        assertEquals("(十五)", m.group("N"));
        assertEquals("ある施設基準", m.group("H"));
    }

    @Test
    public void test区分番号() {
        String s = Pat.numberHeader(Pat.区分番号);
        Pattern pattern = Pattern.compile(s);
        Matcher m = pattern.matcher("Ａ００１－２ 初診料");
        assertTrue(m.matches());
        assertEquals("Ａ００１－２", m.group("N"));
        assertEquals("初診料", m.group("H"));
    }
    
    @Test
    public void testIdentifier() {
        assertEquals("3", Pat.数字id.apply("３"));
        assertEquals("3", Pat.数字id.apply("(３)"));
        assertEquals("3", Pat.数字id.apply("（３）"));
        assertEquals("A001-2-3", Pat.区分番号id.apply("Ａ００１－２－３"));
        assertEquals("00-1", Pat.区分番号id.apply("区分００の１"));
        assertEquals("1", Pat.漢数字id.apply("（一）"));
        assertEquals("2", Pat.漢数字id.apply("（二）"));
        assertEquals("3", Pat.漢数字id.apply("（三）"));
        assertEquals("4", Pat.漢数字id.apply("（四）"));
        assertEquals("5", Pat.漢数字id.apply("（五）"));
        assertEquals("6", Pat.漢数字id.apply("（六）"));
        assertEquals("7", Pat.漢数字id.apply("（七）"));
        assertEquals("8", Pat.漢数字id.apply("（八）"));
        assertEquals("10", Pat.漢数字id.apply("（十）"));
        assertEquals("11", Pat.漢数字id.apply("（十一）"));
        assertEquals("12", Pat.漢数字id.apply("（十ニ）"));
        assertEquals("13", Pat.漢数字id.apply("（十三）"));
        assertEquals("19", Pat.漢数字id.apply("（十九）"));
        assertEquals("20", Pat.漢数字id.apply("（二十）"));
        assertEquals("21", Pat.漢数字id.apply("（二十一）"));
        assertEquals("22", Pat.漢数字id.apply("（二十二）"));
        assertEquals("29", Pat.漢数字id.apply("（二十九）"));
        assertEquals("20-3-6", Pat.漢数字id.apply("（二十の三の六）"));
        assertEquals("29-3-6", Pat.漢数字id.apply("（二十九の三の六）"));
        assertEquals("2", Pat.アイウid.apply("（イ）"));
        assertEquals("1", Pat.イロハid.apply("（イ）"));
        assertEquals("1", Pat.アイウid.apply("（ア）"));
        assertEquals("48", Pat.アイウid.apply("ン"));
        assertEquals("49", Pat.アイウid.apply("アア"));
        assertEquals("50", Pat.アイウid.apply("アイ"));
        assertEquals("48", Pat.イロハid.apply("ン"));
        assertEquals("49", Pat.イロハid.apply("イイ"));
        assertEquals("50", Pat.イロハid.apply("イロ"));
        assertEquals("29", Pat.アイウid.apply("ヘ")); // カタカナ
        assertEquals("29", Pat.アイウid.apply("へ")); // ひらがな
        assertEquals("1", Pat.丸数字id.apply("①"));
        assertEquals("27", Pat.丸数字id.apply("㉗"));
        assertEquals("10-3-6x10-10", Pat.漢数字id.apply("十の三の六から十の十まで"));
        assertEquals("19-3-6x12-8", Pat.漢数字id.apply("十九の三の六から十二の八まで"));
        assertEquals("19-3-6x32-12", Pat.漢数字id.apply("十九の三の六及び三十二の十二"));
    }
    
    @Test
    public void testRegex() {
        String s = "123456789A";
        assertEquals("A", s.replaceFirst("(.)(.)(.)(.)(.)(.)(.)(.)(.)(.)", "$10"));
        assertEquals("10", s.replaceFirst("(.)(.)(.)(.)(.)(.)(.)(.)(.)(.)", "$1\\0"));
    }
    
    @Test
    public void testRegex除外() {
        Pattern pat = Pattern.compile("＜(?!通則).*＞");
        assertFalse(pat.matcher("＜通則＞").matches());
        assertTrue(pat.matcher("＜処置料＞").matches());
    }

    @Test
    public void test区分順序化() {
        assertEquals("B00001-00001", Pat.区分順序化("B001-1"));
        assertEquals("B00001-00001", Pat.区分順序化("Ｂ００１－１"));
        assertEquals("B00001-00012", Pat.区分順序化("Ｂ００１－１２"));
        assertTrue("B001-2".compareTo("B001-12") > 0);
        assertTrue(Pat.区分順序化("B001-2").compareTo(Pat.区分順序化("B001-12")) < 0);
    }
    
    static final Pattern 施設基準様式 = Pattern.compile(Pat.施設基準様式名パターン);
    static String y(String s) {
        Matcher m = 施設基準様式.matcher(s);
        if (!m.find())
            return null;
        return Pat.正規化(m.group());
    }

    @Test
    public void test施設基準様式名パターン() {
        assertEquals("T3-S4", y("別添３の別紙４"));
//        assertEquals("T7", y("別添７"));
        assertEquals("T6-S1-2-3", y("別 添６ の 別紙１の２の３"));
        assertEquals("T7-Y1-2-3", y("別添 ７ の 様式 １ の ２ の ３"));
        assertEquals("T2-Y1", y("別添2の様式１"));
    }
}
