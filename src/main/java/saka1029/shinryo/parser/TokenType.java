package saka1029.shinryo.parser;

import java.util.regex.Pattern;

public class TokenType implements Syntax {
	static final String PREFIX = "^\\s*(?<N>";
	static final String SUFFIX = ")\\s+(<H>.*)\\s*$";
	public final String name;
	public final NumberType ntype;
	public final Pattern pattern;
	
	TokenType(String name, NumberType ntype, String pattern) {
		this.name = name;
		this.ntype = ntype;
		this.pattern = Pattern.compile(PREFIX + pattern + SUFFIX);
	}
	
	public static TokenType からまで(String name, NumberType ntype) {
		return new TokenType(name, ntype,
			"(?<F>%1$s)((から|及び)(?<T>%1$s)(まで)?)?".formatted(ntype.fullPattern.pattern()));
	}
	
	public static TokenType の(String name, NumberType ntype, String conj) {
		String single = "%1$s(%2$s%1$s)*".formatted(ntype.fullPattern.pattern(), conj);
		return new TokenType(name, ntype,
			"(?<F>%1$s)((から|及び)(?<T>%1$s)(まで)?)?".formatted(single));
	}
	
	@Override
	public String toString() {
		return name;
	}

}
