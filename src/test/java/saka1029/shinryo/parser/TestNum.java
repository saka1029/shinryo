package saka1029.shinryo.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

import saka1029.shinryo.common.Logging;

public class TestNum {

    static { Logging.init(); } 
    static final Logger logger = Logger.getLogger(TestNum.class.getSimpleName());

    @Test
    public void testFromTo() {
        String s = Pat.numberHeader(Pat.fromTo(Pat.repeat(Pat.数字, "の")));
        Pattern pattern = Pattern.compile(s);
        Matcher m = pattern.matcher("１の２の３から１の３の４まで 削除");
        logger.info(s);
        assertTrue(m.matches());
        assertEquals("１の２の３から１の３の４まで", m.group("N"));
        assertEquals("削除", m.group("H"));
    }

}
