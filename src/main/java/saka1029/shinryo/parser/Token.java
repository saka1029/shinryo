package saka1029.shinryo.parser;

import java.util.ArrayList;
import java.util.List;

public record Token(
    TokenType type,
    String id,
    String number,
    String header,
    String fileName,
    int pageNo,
    int lineNo,
    int indent,
    List<String> body) {
	
    public Token(TokenType type, String number, String header, String fileName, int pageNo, int lineNo, int indent) {
        this(type, type.id(number), number, header, fileName, pageNo, lineNo, indent, new ArrayList<>());
    }
    
    @Override
    public String toString() {
        return "Token(%s, %s, %s, %s:%s:%s:%s, %s)".formatted(
            type.name(), number, header, fileName, pageNo, lineNo, indent, body);
    }
}
