package saka1029.shinryo.parser;

import java.io.IOException;
import java.util.List;

public abstract class Parser {
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
	
	boolean eat(TokenType expected) {
		if (token == null)
			return false;
		if (token.type().equals(expected)) {
			eaten = token;
			token = get();
			return true;
		}
		return false;
	}
	
	boolean eatChild(Node parent, TokenType expected) {
		if (token == null)
			return false;
		if (token.indent() > parent.token().indent() && token.type().equals(expected)) {
			eaten = token;
			token = get();
			return true;
		}
		return false;
	}
	
	Node add(Node parent, Token token) {
		Node child = new Node(token);
		parent.children().add(child);
		return child;
	}
	
	public abstract List<TokenType> types();
	public abstract void parse(Node parent);
	
	public Node parse(List<Token> tokens) {
		this.tokens = tokens;
		this.max = tokens.size();
		this.token = get();
		Node root = new Node(null);
		parse(root);
		return root;
	}
	
	public Node parse(String inTxtFile) throws IOException {
	    List<Token> tokens = TokenReader.read(types(), inTxtFile);
	    return parse(tokens);
	}
}
