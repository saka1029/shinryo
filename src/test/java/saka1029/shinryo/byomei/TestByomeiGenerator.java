package saka1029.shinryo.byomei;

import java.io.IOException;
import java.nio.file.Path;

import org.junit.Test;

import saka1029.shinryo.common.Param;

public class TestByomeiGenerator {

    @Test
    public void testGenerate() throws IOException {
        String[] inFiles = Param.files(Path.of("in/byomei"), ".txt");
        String outFile = "test/byomei_dict.js";
        new ByomeiGenerator().generate(outFile, inFiles);
    }

}
