package saka1029.shinryo.common;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

public class TestParam {

    @Test
    public void test年度() throws IOException {
        Param param = Param.年度("04");
        assertEquals("令和", param.元号);
        assertEquals("04", param.年度);
        assertEquals("令和", param.旧元号);
        assertEquals("02", param.旧年度);
    }

    @Test
    public void testRead() throws IOException {
        Param param = Param.read("in/04/param.json");
        assertEquals("令和", param.元号);
        assertEquals("04", param.年度);
        assertEquals("令和", param.旧元号);
        assertEquals("02", param.旧年度);
    }

}
