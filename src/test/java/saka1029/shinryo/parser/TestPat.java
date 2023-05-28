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
        String s = Pat.numberHeader(Pat.fromTo(Pat.repeat(Pat.数字, "の")));
        Pattern pattern = Pattern.compile(s);
        Matcher m = pattern.matcher("１の２の３から１の３の４まで 削除");
//        logger.info(s);
        assertTrue(m.matches());
        assertEquals("１の２の３から１の３の４まで", m.group("N"));
        assertEquals("削除", m.group("H"));
    }

    @Test
    public void test別表第漢字() {
        String s = Pat.numberHeader("別表第" + Pat.repeat(Pat.漢数字, "の"));
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
        logger.info(s);
        assertTrue(m.matches());
        assertEquals("Ａ００１－２", m.group("N"));
        assertEquals("初診料", m.group("H"));
    }

}
