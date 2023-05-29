package saka1029.shinryo.common;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;

public class Logging {

    
    static final Formatter MY_FORMATTER = new Formatter() {
        @Override
        public String format(LogRecord record) {
            String loggerName = record.getLoggerName();
//            String loggerName = record.getLoggerName().replaceFirst("^.*\\.", "");
            return String.format("%1$tY-%1$tm-%1$td %1$tT.%1$tL %3$s %4$s: %5$s%6$s%n",
                new Date(record.getMillis()), record.getSourceClassName(),
                loggerName, record.getLevel(), record.getMessage(),
                record.getThrown() == null ? "" : " " + record.getThrown());
        }
    };

    public static void init() {
        init(Level.INFO);
    }

    /**
     * デバッグ用のログ出力です。
     * JUnitでテストするときに呼び出します。
     * アプリケーションの中で呼んではいけません。
     */
    public static void init(Level level) {
        Logger root = Logger.getLogger("");
        for (Handler h : root.getHandlers())
            root.removeHandler(h);
        Formatter formatter = MY_FORMATTER;
        Handler handler = new StreamHandler(System.err, formatter);
        try {
            handler.setEncoding("utf-8");
        } catch (SecurityException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        root.addHandler(handler);
        root.setLevel(level);
        handler.setLevel(level);
    }
}
