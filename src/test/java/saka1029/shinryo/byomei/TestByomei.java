package saka1029.shinryo.byomei;

import java.io.IOException;
import java.nio.file.Path;

import org.junit.Test;

import saka1029.shinryo.common.Param;

public class TestByomei {

    @Test
    public void testByomei() throws IOException {
        Path path = Path.of("in/06/b/txt");
        String[] files = Param.files(path, ".txt");
        for (String e : files)
            System.out.println(e);
    }
}
