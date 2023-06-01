package saka1029.shinryo.common;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class TextWriter implements Closeable {
    
    static final String NL = "\n";
    final PrintWriter writer;

    public TextWriter(String outTxtFile) throws IOException {
        this.writer = new PrintWriter(outTxtFile, StandardCharsets.UTF_8);
    }
    
    public TextWriter(Path outTxtFile) throws IOException {
        this(outTxtFile.toString());
    }
    
    public void println(String format, Object... args) {
        writer.printf(format + NL, args);
    }

    @Override
    public void close() throws IOException {
        writer.close();
    }
}
