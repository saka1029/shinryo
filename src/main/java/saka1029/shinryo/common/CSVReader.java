package saka1029.shinryo.common;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * <pre>
 * [SYNTAX]
 * file        ::= record { newline record } [newline]
 * record      ::= field { ',' field }
 * field       ::= { quoted | unquoted }
 * quoted      ::= '"' { textdata | ',' | newline | '"' '"' | '\' '"' } '"'
 * unquoted    ::= { textdata }
 * newline     ::= '\r' [ '\n' ] | '\n'
 * textdata    ::= any character except '\r', '\n', ',', '"'
 * </pre>
 *
 * RFC4180 Common Format and MIME Type for Comma-Separated Values (CSV) Files
 *
 * <pre>
 * [RFC4180 ABNF]
 * file = [header CRLF] record *(CRLF record) [CRLF]
 * header = name *(COMMA name)
 * record = field *(COMMA field)
 * name = field
 * field = (escaped / non-escaped)
 * escaped = DQUOTE *(TEXTDATA / COMMA / CR / LF / 2DQUOTE) DQUOTE
 * non-escaped = *TEXTDATA
 * COMMA = %x2C
 * CR = %x0D ;as per section 6.1 of RFC 2234 [2]
 * DQUOTE =  %x22 ;as per section 6.1 of RFC 2234 [2]
 * LF = %x0A ;as per section 6.1 of RFC 2234 [2]
 * CRLF = CR LF ;as per section 6.1 of RFC 2234 [2]
 * TEXTDATA =  %x20-21 / %x23-2B / %x2D-7E
 * </pre>
 *
 */
public class CSVReader implements Closeable {

    final BufferedReader reader;
    int ch;
    List<String> line;
    final StringBuilder field = new StringBuilder();

    public CSVReader(BufferedReader reader) throws IOException {
        this.reader = reader;
        this.ch = get();
    }

    public CSVReader(Reader reader) throws IOException {
        this(new BufferedReader(reader));
    }

    public CSVReader(Path path, Charset encoding) throws IOException {
        this(Files.newBufferedReader(path, encoding));
    }

    public CSVReader(Path path) throws IOException {
        this(path, Charset.defaultCharset());
    }

    public CSVReader(String csv) throws IOException {
        this(new StringReader(csv));
    }

    int get() throws IOException {
        return ch = reader.read();
    }

    void put(int c) {
        field.append((char) c);
    }

    void putGet(int c) throws IOException {
        put(c);
        get();
    }

    boolean is(int... cs) {
        for (int c : cs)
            if (c == ch)
                return true;
        return false;
    }

    boolean match(int c) throws IOException {
        if (ch != c)
            return false;
        if (ch != -1)
            get();
        return true;
    }

    void quoted() throws IOException {
        get(); // skip '"'
        while (true)
            if (match(-1))  // 引用符が開いたままEOFとなるケース（あえてエラーとしない）
                break;
            else if (match('"'))
                if (match('"')) // 引用符自体の指定
                    put('"');
                else
                    break;      // 単独の引用符
            else if (match('\\'))
                putGet(ch);
            else
                putGet(ch);
    }

    void unquoted() throws IOException {
        while (!is(-1, ',', '"', '\r', '\n'))
            putGet(ch);
    }

    void field() throws IOException {
        field.setLength(0);
        while (true)
            if (is('"'))
                quoted();
            else  if (!is(-1, ',', '\r', '\n'))
                unquoted();
            else
                break;
        line.add(field.toString());
    }

    public List<String> readLine() throws IOException {
        if (match(-1))
            return null;
        line = new ArrayList<>();
        field();
        while (match(','))
            field();
        if (match(-1) || match('\n'))
            /* OK */ ;
        else if (match('\r'))
            match('\n');
        else
            throw new RuntimeException("CR, LF, CRLF or EOF expected but '" + (char)ch + "'");
        return line;
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }
}
