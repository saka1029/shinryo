package saka1029.shinryo.parser;

import java.util.ArrayList;
import java.util.List;

public class Token {
	public final TokenType type;
	public final String number;
	public final String header;
	public final String id;
	public final List<String> body = new ArrayList<>();
	
	public Token(TokenType type, String number, String header, String id) {
		this.type = type;
		this.number = number;
		this.header = header;
		this.id = id;
	}

    @Override
    public String toString() {
        return "Token [type=" + type + ", number=" + number + ", header=" + header + ", id=" + id + ", body=" + body
            + "]";
    }


}
