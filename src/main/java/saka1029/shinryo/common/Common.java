package saka1029.shinryo.common;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class Common {

    static final Formatter MY_FORMATTER = new Formatter() {
        @Override
        public String format(LogRecord record) {
            String loggerName = record.getLoggerName();
            return String.format("%1$tY-%1$tm-%1$td %1$tT.%1$tL %3$s %4$s: %5$s%6$s%n",
                new Date(record.getMillis()), record.getSourceClassName(),
                loggerName, record.getLevel(), record.getMessage(),
                record.getThrown() == null ? "" : " " + record.getThrown());
        }
    };

    public static Logger logger(Class<?> clazz) {
        initLogger();
        return Logger.getLogger(clazz.getSimpleName());
    }
    
    public static Logger logger(Class<?> clazz, Level level) {
        Logger logger = logger(clazz);
        logger.setLevel(level);
        return logger;
    }

    static void initLogger() {
        if (!System.out.charset().equals(StandardCharsets.UTF_8))
            System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        if (!System.err.charset().equals(StandardCharsets.UTF_8))
            System.setErr(new PrintStream(System.err, true, StandardCharsets.UTF_8));
        for (Handler h : Logger.getLogger("").getHandlers())
            if (!h.getFormatter().equals(MY_FORMATTER))
                h.setFormatter(MY_FORMATTER);
    }
    
    public static boolean inEclipse() {
        return System.getProperty("java.class.path").contains("eclipse");
    }
    
    public static String methodName() {
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        return elements[2].getMethodName();
    }
}
