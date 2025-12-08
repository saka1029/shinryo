package saka1029.shinryo.byomei;

import static org.junit.Assert.assertArrayEquals;

import java.io.IOException;

import org.junit.Test;

import saka1029.shinryo.common.Param;

public class TestByomei {

    @Test
    public void testByomei() throws IOException {
        Param param = Param.of("in", "debug/html", "06");
        String[] inFiles = param.inFiles("b", "txt", ".txt");
        assertArrayEquals(new String[] {
            "in/06/b/txt/b_20250601.txt",
            "in/06/b/txt/z_20250601.txt"},
            inFiles);
    }
}
