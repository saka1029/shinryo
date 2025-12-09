package saka1029.shinryo.byomei;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.List;

import saka1029.shinryo.common.CSVReader;
import saka1029.shinryo.common.TextWriter;

public class ByomeiGenerator {

    static final Charset 文字コード = Charset.forName("Shift_JIS");
    static final String 未コード化傷病名コード = "0000999";

    void generate(String outFile, String[] inFiles) throws IOException {
        try (TextWriter w = new TextWriter(outFile)) {
            w.println("const BYOMEI = {");
            for (String in : inFiles) {
                Path path = Path.of(in);
                int codeCol, nameCol;
                switch (path.getFileName().toString().toLowerCase().charAt(0)) {
                    case 'b': codeCol = 2; nameCol = 5; break;
                    case 'z': codeCol = 2; nameCol = 6; break;
                    default: throw new RuntimeException("unknown file type: " + path);
                }
                try (CSVReader r = new CSVReader(path, 文字コード)) {
                    List<String> line;
                    while ((line = r.readLine()) != null)
                        if (!line.get(codeCol).equals(未コード化傷病名コード))
                            w.println("  \"%s\":\"%s\",", line.get(nameCol), line.get(codeCol));
                }
            }
            w.println("};");
        }
    }
}
