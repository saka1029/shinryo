package saka1029.shinryo.common;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Test;

public class TestCommon {
    
    static Logger logger = Common.logger(TestCommon.class);

    @Test
    public void testInEclipse() {
        logger.info("in eclipse: " + Common.inEclipse());
    }

    @Test
    public void testLogging() {
        logger.setLevel(Level.FINEST);
        logger.finest("日本語を出力");
        logger.fine("日本語を出力");
        logger.info("日本語を出力");
        logger.warning("日本語を出力");
    }

}
