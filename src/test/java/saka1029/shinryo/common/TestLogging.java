package saka1029.shinryo.common;

import static org.junit.Assert.*;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Test;

public class TestLogging {
    
    static { Logging.init(Level.FINEST); }
    static Logger logger = Logger.getLogger(TestLogging.class.getSimpleName());

    @Test
    public void testLogging() {
        logger.finest("日本語を出力");
        logger.fine("日本語を出力");
        logger.info("日本語を出力");
        logger.warning("日本語を出力");
    }

}
