package saka1029.shinryo.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

import saka1029.shinryo.common.Logging;

public class TestPat {

    static { Logging.init(); } 
    static final Logger logger = Logger.getLogger(TestPat.class.getSimpleName());

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
        assertEquals("19", Pat.漢数字id.apply("（十九）"));
        assertEquals("22", Pat.漢数字id.apply("（二十二）"));
        assertEquals("29", Pat.漢数字id.apply("（二十九）"));
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
        assertEquals("10-3-6+10-10", Pat.漢数字id.apply("十の三の六から十の十まで"));
        assertEquals("19-3-6+12-8", Pat.漢数字id.apply("十九の三の六から十二の八まで"));
        assertEquals("19-3-6+32-12", Pat.漢数字id.apply("十九の三の六及び三十二の十二"));
    }
    
    @Test
    public void testRegex() {
        String s = "123456789A";
        assertEquals("A", s.replaceFirst("(.)(.)(.)(.)(.)(.)(.)(.)(.)(.)", "$10"));
        assertEquals("10", s.replaceFirst("(.)(.)(.)(.)(.)(.)(.)(.)(.)(.)", "$1\\0"));
    }

}
