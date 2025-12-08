package saka1029.shinryo.byomei;

import static org.junit.Assert.assertArrayEquals;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Test;

import saka1029.shinryo.common.CSVReader;
import saka1029.shinryo.common.Common;
import saka1029.shinryo.common.HashEncoder;
import saka1029.shinryo.common.Param;

public class TestByomei {

    static final Logger logger = Common.logger(TestByomei.class);

    static final Charset CHARSET = Charset.forName("Shift_JIS");

    static void readByomei(Path file, HashEncoder<String> encoder, int codeCol, int nameCol) throws IOException {
        try (CSVReader reader = new CSVReader(file, CHARSET)) {
            List<String> line;
            while ((line = reader.readLine()) != null)
                if (!line.get(codeCol).equals("0000999"))
                    encoder.put(line.get(nameCol), line.get(codeCol));
        }
    }

    static Predicate<List<HashEncoder.Entry<String>>> FILTER = list -> 
        list.stream().filter(e -> e.data().length() == 7).count() == 1;

    static final Level PRINT_LEVEL = Level.FINE;

    static void printEncode(HashEncoder<String> encoder, String byomei) {
        logger.log(PRINT_LEVEL, "%s :%n".formatted(byomei));
        var encoded = encoder.encode(byomei, FILTER);
        for (var e : encoded) {
            StringBuilder sb = new StringBuilder();
            for (var f : e) {
                sb.append(" %s:%s".formatted(
                    f.data(), byomei.substring(f.start(), f.end())));
            }
            logger.log(PRINT_LEVEL, sb.toString());
        }
    }

    @Test
    public void testByomei() throws IOException {
        Param param = Param.of("in", "debug/html", "06");
        String[] inFiles = param.inFiles("b", "txt", ".txt");
        assertArrayEquals(new String[] {
            "in/06/b/txt/b_20250601.txt",
            "in/06/b/txt/z_20250601.txt"},
            inFiles);
        HashEncoder<String> encoder = new HashEncoder<>();
        logger.info("read start");
        for (String file : inFiles) {
            Path path = Path.of(file);
            String name = path.getFileName().toString().toLowerCase();
            if (name.startsWith("b"))
                readByomei(path, encoder, 2, 5);
            else if (name.startsWith("z"))
                readByomei(path, encoder, 2, 6);
            else
                throw new RuntimeException("unknown file name: " + name);
        }
        logger.info("read end");
        logger.info("encode start");
        Path micode = Path.of(param.inFile("b", "micode.txt"));
        try (CSVReader reader = new CSVReader(micode)) {
            List<String> line;
            while ((line = reader.readLine()) != null)
                printEncode(encoder, line.get(1));
        }
        logger.info("encode end");
    }
}
