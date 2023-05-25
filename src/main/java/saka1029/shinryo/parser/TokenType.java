package saka1029.shinryo.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TokenType implements Syntax {
	static final String PREFIX = "^\\s*(?<N>";
	static final String SUFFIX = ")\\s+(?<H>.*)\\s*$";
	public final String name;
	public final ItemPattern itemPattern;
	public final Pattern pattern;
	
	TokenType(String name, ItemPattern itemPattern, String pattern) {
		this.name = name;
		this.itemPattern = itemPattern;
		this.pattern = Pattern.compile(PREFIX + pattern + SUFFIX);
	}
	
	public static TokenType simple(String name, ItemPattern itemPattern) {
		return new TokenType(name, itemPattern,
			"(?<F>%1$s)(?<T>)".formatted(itemPattern.pattern));
	}
	
	public static TokenType fromTo(String name, ItemPattern itemPattern) {
		return new TokenType(name, itemPattern,
			"(?<F>%1$s)((から|及び)(?<T>%1$s)(まで)?)?".formatted(itemPattern.pattern));
	}
	
	public Token match(String line) {
	    Matcher m = pattern.matcher(line);
	    if (!m.matches())
	        return null;
	    String number = m.group("N");
	    String header = m.group("H");
	    String from = m.group("F");
	    String to = m.group("T");
	    String id = itemPattern.id(from) + (to.equals("") ? "" : "+" + itemPattern.id(to));
	    return new Token(this, number, header, id);
	}

	@Override
	public String toString() {
		return name;
	}

}
