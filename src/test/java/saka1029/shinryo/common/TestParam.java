package saka1029.shinryo.common;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Logger;

import org.junit.Test;

public class TestParam {

    static final Logger logger = Common.logger(TestParam.class);

    @Test
    public void testFiles() throws IOException {
        String[] files = Param.files(Path.of("in/04/i/pdf/y"), ".pdf");
        assertEquals(2, files.length);
        assertEquals(Path.of("in/04/i/pdf/y/000907839.pdf"), Path.of(files[0]));
        assertEquals(Path.of("in/04/i/pdf/y/000985121.pdf"), Path.of(files[1]));
    }

    @Test
    public void testOf() throws IOException {
        Param p = Param.of("04");
        assertEquals("令和", p.元号);
        assertEquals("04", p.年度);
        assertEquals("令和", p.旧元号);
        assertEquals("02", p.旧年度);
        assertEquals(Path.of("in/04/i/txt/k.txt"), Path.of(p.inFile("i", "txt/k.txt")));
        assertEquals(Path.of("in/04/i/txt/t.txt"), Path.of(p.inFile("i", "txt/t.txt")));
        assertEquals(Path.of("in/04/i/txt/y.txt"), Path.of(p.inFile("i", "txt/y.txt")));
        assertEquals(Path.of("out/04/i"), Path.of(p.outDir("i")));
        assertEquals(Path.of("out/04/i/index.html"), Path.of(p.outFile("i", "index.html")));
        assertEquals(Path.of("out/04"), Path.of(p.outDir()));
        assertEquals(Path.of("out/04/index.html"), Path.of(p.outFile("index.html")));
    }

    @Test
    public void testOfFull() throws IOException {
        Param p = Param.of("IN", "OUT", "04");
        assertEquals("令和", p.元号);
        assertEquals("04", p.年度);
        assertEquals("令和", p.旧元号);
        assertEquals("02", p.旧年度);
        assertEquals(Path.of("IN/04/i/txt/k.txt"), Path.of(p.inFile("i", "txt/k.txt")));
        assertEquals(Path.of("IN/04/i/txt/t.txt"), Path.of(p.inFile("i", "txt/t.txt")));
        assertEquals(Path.of("IN/04/i/txt/y.txt"), Path.of(p.inFile("i", "txt/y.txt")));
        assertEquals(Path.of("OUT/04/i"), Path.of(p.outDir("i")));
        assertEquals(Path.of("OUT/04/i/index.html"), Path.of(p.outFile("i", "index.html")));
        assertEquals(Path.of("OUT/04"), Path.of(p.outDir()));
        assertEquals(Path.of("OUT/04/index.html"), Path.of(p.outFile("index.html")));
    }
    
    @Test
    public void testPrevious() {
        Param p = Param.of("IN", "OUT", "04");
        Param previous = p.previous();
        assertEquals("02", previous.年度);
        assertEquals("令和", previous.元号);
        assertEquals("IN", previous.inDir);
        assertEquals("OUT", previous.outDir);
        Param preprepre = p.previous().previous().previous();
        assertEquals("30", preprepre.年度);
        assertEquals("平成", preprepre.元号);
        assertEquals("IN", preprepre.inDir);
        assertEquals("OUT", preprepre.outDir);
    }

    @Test
    public void testInFile() throws IOException {
        Param param = Param.of("in", "out", "06");
        assertArrayEquals(new String[]{"in/06/i/pdf/k/001218731.pdf"},
            param.inFiles("i", "pdf/k", ".pdf"));
        assertEquals("in/06/i/txt/k.txt", param.inFile("i", "txt/k.txt"));

    }
}
