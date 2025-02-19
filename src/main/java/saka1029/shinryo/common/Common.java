package saka1029.shinryo.common;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class Common {

    static final Formatter MY_FORMATTER= new Formatter() {
        @Override public String format(LogRecord record){
            String loggerName=record.getLoggerName();
            return String.format("%1$tY-%1$tm-%1$td %1$tT.%1$tL %3$s %4$s: %5$s%6$s%n",
                new Date(record.getMillis()), record.getSourceClassName(), loggerName,
                record.getLevel(), record.getMessage(), record.getThrown()==null ? "" : " " + record.getThrown());
            }
        };

    public static Logger logger(Class<?> clazz) {
        initLogger();
        return Logger.getLogger(clazz.getName());
    }

    /**
     * このメソッドを呼んでもレベルは設定できない。
     */
    public static Logger logger(Class<?> clazz, Level level) {
        Logger logger = logger(clazz);
        logger.setLevel(level);
        return logger;
    }

    static boolean INIT_LOGGER = false;

    /**
     * System.out, System.errの文字セットをUTF-8に変更する。 すべてのロガーのフォーマットをMY_FORMATに変更する。
     * 
     * System.out, System.errのデフォルト文字セットは (1)コマンドプロンプトの場合はコードページに依存する。
     * （コードページ932のときMS932、65001のときUTF-8） (2)Eclipseの場合はwindows-31jとなる。
     */
    public static void initLogger() {
        if (INIT_LOGGER)
            return;
        // if (!System.out.charset().equals(StandardCharsets.UTF_8))
            System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        // if (!System.err.charset().equals(StandardCharsets.UTF_8))
            System.setErr(new PrintStream(System.err, true, StandardCharsets.UTF_8));
        for (Handler h : Logger.getLogger("").getHandlers())
            if (!h.getFormatter().equals(MY_FORMATTER))
                h.setFormatter(MY_FORMATTER);
        INIT_LOGGER = true;
    }

    public static boolean inEclipse() {
        return System.getProperty("java.class.path").contains("eclipse");
    }

    public static String methodName() {
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        return elements[2].getMethodName();
    }

    static final FileVisitor<Path> DELETE_DIRECTORY = new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path path, BasicFileAttributes basicFileAttributes) throws IOException {
                Files.delete(path);
                return FileVisitResult.CONTINUE;
            }
            @Override
            public FileVisitResult postVisitDirectory(Path directory, IOException ioException) throws IOException {
                Files.delete(directory);
                return FileVisitResult.CONTINUE;
            }
        };

    public static void deleteDirectory(String dir) throws IOException {
        Path path = Path.of(dir);
        if (Files.exists(path))
            Files.walkFileTree(path, DELETE_DIRECTORY);
    }
    
    public static void copyTree(String inDir, String outDir) throws IOException {
        Path inPath = Path.of(inDir);
        Path outPath = Path.of(outDir);
        Files.walkFileTree(inPath, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attrs) throws IOException {
                Files.createDirectories(outPath.resolve(inPath.relativize(dir)));
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
                Files.copy(file, outPath.resolve(inPath.relativize(file)), StandardCopyOption.REPLACE_EXISTING);
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
