package saka1029.shinryo.parser;

import java.util.ArrayList;
import java.util.List;

public class Token {
    public final TokenType type;
    /**
     * HTMLにおけるid属性値:
     * 
     * HTML4.01: id属性値は「アルファベット([A-Za-z])で開始し、任意の数のアルファベット、
     * 数字、([0-9])、ハイフン(-)、アンダースコア(_)、コロン(:)、ピリオド(.)のみで記述する必要がある。
     * 大文字/小文字を区別する(CS)。この名前は文書中で一意でなければならない(MUST)。」
     * 
     * HTML5: HTML Living Standard (通称 HTML 5) ではid属性値、class属性値に対して
     * 空白文字を除く全ての文字を使用できます。 id属性値の「名前が一意でなければならない」のは HTML 4.01 と同様です。
     */
    public final String id; // 兄弟ノードを識別する識別子。[a-zA-z0-9+-]*
    public final String number;
    public final String header;
    public final String fileName;
    public final int pageNo;
    public final int lineNo;
    public final int indent;
    public final List<String> body;

    public Token(TokenType type, String number, String header, String fileName, int pageNo, int lineNo, int indent,
        List<String> body) {
        this.type = type;
        this.id = type.id(number);
        this.number = number;
        this.header = header;
        this.fileName = fileName;
        this.pageNo = pageNo;
        this.lineNo = lineNo;
        this.indent = indent;
        this.body = body;
    }

    public Token(TokenType type, String number, String header, String fileName, int pageNo, int lineNo, int indent) {
        this(type, number, header, fileName, pageNo, lineNo, indent, new ArrayList<>());
    }

    /**
     * Token orgを複製する。 ただしtype, number, header, bodyは変更する。
     */
    public Token(TokenType type, String number, String header, List<String> body, Token org) {
        this(type, number, header, org.fileName, org.pageNo, org.lineNo, org.indent, body);
    }

    /**
     * headerの内、最初の空白より前を返します。
     * <pre>
     * header         header0
     * -------------  -------
     * "aaa"          "aaa"
     * "aaa bbb"      "aaa"
     * "aaa bbb ccc"  "aaa"
     * </pre>
     */
    public String header0() {
        return header.replaceFirst("\\s.*", "");
    }
    
    /**
     * headerの内、最初の空白より後を返します。
     * <pre>
     * header         header0
     * -------------  -------
     * "aaa"          ""
     * "aaa bbb"      "bbb"
     * "aaa bbb ccc"  "bbb ccc"
     * </pre>
     */
    public String header1() {
        return header.replaceFirst("\\S*\\s*", "");
    }

    @Override
    public String toString() {
        return "Token(%s, %s, %s, %s:%s:%s:%s, %s)".formatted(
            type.name, number, header, fileName, pageNo, lineNo, indent, body);
    }
}
