package saka1029.shinryo.common;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Logger;

import org.junit.Test;

import saka1029.shinryo.parser.TestNode;

public class TestParam {

    static { Logging.init(); } 
    static final Logger logger = Logger.getLogger(TestNode.class.getSimpleName());

    @Test
    public void testFiles() throws IOException {
        String[] files = Param.files(Path.of("in/04/i/txt"), ".txt");
        assertEquals(2, files.length);
        assertEquals("in/04/i/txt/ke.txt", files[0]);
        assertEquals("in/04/i/txt/te.txt", files[1]);
    }

    @Test
    public void testOf() throws IOException {
        Param p = Param.of("04");
        assertEquals("令和", p.元号);
        assertEquals("04", p.年度);
        assertEquals("令和", p.旧元号);
        assertEquals("02", p.旧年度);
        assertEquals("in/04/i/txt/k.txt", p.txt("i", "k"));
        assertEquals("in/04/i/txt/t.txt", p.txt("i", "t"));
        assertEquals("in/04/i/txt/y.txt", p.txt("i", "y"));
        assertEquals("out/04/i", p.outDir("i"));
        assertEquals("out/04/i/index.html", p.outFile("i", "index.html"));
    }

    @Test
    public void testOfFull() throws IOException {
        Param p = Param.of("IN", "OUT", "04");
        assertEquals("令和", p.元号);
        assertEquals("04", p.年度);
        assertEquals("令和", p.旧元号);
        assertEquals("02", p.旧年度);
        assertEquals("IN/04/i/txt/k.txt", p.txt("i", "k"));
        assertEquals("IN/04/i/txt/t.txt", p.txt("i", "t"));
        assertEquals("IN/04/i/txt/y.txt", p.txt("i", "y"));
        assertEquals("OUT/04/i", p.outDir("i"));
        assertEquals("OUT/04/i/index.html", p.outFile("i", "index.html"));
    }
}
