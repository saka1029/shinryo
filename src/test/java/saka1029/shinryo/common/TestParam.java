package saka1029.shinryo.common;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
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
        assertEquals(Path.of("in/04/i/txt/ke.txt"), Path.of(files[0]));
        assertEquals(Path.of("in/04/i/txt/te.txt"), Path.of(files[1]));
    }

    @Test
    public void testOf() throws IOException {
        Param p = Param.of("04");
        assertEquals("令和", p.元号);
        assertEquals("04", p.年度);
        assertEquals("令和", p.旧元号);
        assertEquals("02", p.旧年度);
        assertEquals(Path.of("in/04/i/txt/k.txt"), Path.of(p.txt("i", "k")));
        assertEquals(Path.of("in/04/i/txt/t.txt"), Path.of(p.txt("i", "t")));
        assertEquals(Path.of("in/04/i/txt/y.txt"), Path.of(p.txt("i", "y")));
        assertEquals(Path.of("out/04/i"), Path.of(p.outDir("i")));
        assertEquals(Path.of("out/04/i/index.html"), Path.of(p.outFile("i", "index.html")));
    }

    @Test
    public void testOfFull() throws IOException {
        Param p = Param.of("IN", "OUT", "04");
        assertEquals("令和", p.元号);
        assertEquals("04", p.年度);
        assertEquals("令和", p.旧元号);
        assertEquals("02", p.旧年度);
        assertEquals(Path.of("IN/04/i/txt/k.txt"), Path.of(p.txt("i", "k")));
        assertEquals(Path.of("IN/04/i/txt/t.txt"), Path.of(p.txt("i", "t")));
        assertEquals(Path.of("IN/04/i/txt/y.txt"), Path.of(p.txt("i", "y")));
        assertEquals(Path.of("OUT/04/i"), Path.of(p.outDir("i")));
        assertEquals(Path.of("OUT/04/i/index.html"), Path.of(p.outFile("i", "index.html")));
    }
    
    @Test
    public void testErr() {
        logger.info(System.getProperty("file.encoding"));
        logger.info(System.getProperty("stderr.encoding"));
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        logger.info(System.out.charset().toString());
        System.out.println("こんにちは");
    }
}
