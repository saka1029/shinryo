package saka1029.shinryo.parser;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

public abstract class Parser {
	static final Logger logger = Logger.getLogger(Parser.class.getName());

	List<Token> tokens;
	int max;
	int index = 0;
	Token token, eaten = null;

	ParseException error(String format, Object... args) {
		return new ParseException(format.formatted(args));
	}
	
	Token get() {
		return index < max ? tokens.get(index++) : null;
	}
	
	boolean is(TokenType expected) {
		if (token == null)
			return false;
		return token.type == expected;
	}

	boolean eat(TokenType expected) {
		if (token == null)
			return false;
		if (token.type.equals(expected)) {
			eaten = token;
			token = get();
			return true;
		}
		return false;
	}
	
	boolean eatChild(Node parent, TokenType expected) {
		if (token == null)
			return false;
		if (token.indent > parent.token.indent && token.type.equals(expected)) {
			eaten = token;
			token = get();
			return true;
		}
		return false;
	}
	
	Node add(Node parent, Token token) {
		return parent.addChild(token);
	}
	
	public abstract List<TokenType> types();
	public abstract void parse(Node parent);
	
	public Node parse(List<Token> tokens) {
		this.tokens = tokens;
		this.max = tokens.size();
		this.token = get();
		Node root = Node.root();
		parse(root);
	    if (token != null)
            logger.warning("未処理のトークン: " + tokens.get(index - 1));
		return root;
	}
	
	public Node parse(String inTxtFile) throws IOException {
	    List<Token> tokens = TokenReader.read(types(), inTxtFile);
	    return parse(tokens);
	}
}
