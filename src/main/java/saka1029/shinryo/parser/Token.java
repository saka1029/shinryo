package saka1029.shinryo.parser;

import java.util.ArrayList;
import java.util.List;

public class Token {
	public final TokenType type;
	public final String number;
	public final String header;
	public final List<String> body = new ArrayList<>();
	
	public Token(TokenType type, String number, String header) {
		this.type = type;
		this.number = number;
		this.header = header;
	}

	@Override
	public String toString() {
		return "Token [type=" + type + " number=" + number + ", header=" + header + ", body=" + body + "]";
	}

}
