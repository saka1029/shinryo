package saka1029.shinryo.parser;

import java.util.regex.Pattern;

public class NumberType {
	
	public final String prefix;
	public final String body;
	public final String suffix;
	public final Pattern numberPattern;
	public final Pattern fullPattern;
	
	public NumberType(String prefix, String body, String suffix) {
		this.prefix = prefix;
		this.body = body;
		this.suffix = suffix;
		this.numberPattern = Pattern.compile(body);
		this.fullPattern = Pattern.compile(prefix + body + suffix);
	}

}
