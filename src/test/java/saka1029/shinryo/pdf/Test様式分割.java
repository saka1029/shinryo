package saka1029.shinryo.pdf;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

// import org.junit.Test;

public class Test様式分割 {

    // @Test
    public void testSplit() throws IOException {
        String outDir = "debug";
        new File(outDir).mkdirs();
        String ye = "in/08/t/txt/ye.txt";
        String ys = "in/08/t/txt/ys.txt";
        様式分割 yoshiki = new 様式分割(ye, ys);
        try (BufferedReader reader = Files.newBufferedReader(Path.of(ye), StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("#")) continue;
                String[] f = line.split(",");
                yoshiki.split(f[0], Path.of(outDir, f[2] + ".pdf"), Integer.parseInt(f[3]), Integer.parseInt(f[4]));
            }
        }
    }

}
