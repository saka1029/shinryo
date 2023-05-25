package saka1029.shinryo.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ItemPattern {
	public final NumberPattern body;
	public final String prefix, suffix, conj;
	public final Pattern pattern;
	
	ItemPattern(String prefix, NumberPattern body, String suffix, String conj, Pattern pattern) {
		this.prefix = prefix;
		this.body = body;
		this.suffix = suffix;
		this.conj = conj;
		this.pattern = pattern;
	}
	
	public static ItemPattern simple(String prefix, NumberPattern body, String suffix) {
		Pattern pattern = Pattern.compile(
            "%1$s%2$s%3$s".formatted(prefix, body.fullPattern, suffix));
	    return new ItemPattern(prefix, body, suffix, null, pattern);
	}
	
	public static ItemPattern conj(String prefix, NumberPattern body, String suffix, String conj) {
		Pattern pattern = Pattern.compile(
            "%1$s%2$s(%4$s%2$s)*%3$s".formatted(prefix, body.fullPattern, suffix, conj));
	    return new ItemPattern(prefix, body, suffix, conj, pattern);
	}
	
	public String id(String itemNumber) {
		Matcher m = body.bodyPattern.matcher(itemNumber);
		if (!m.find())
			throw new RuntimeException(itemNumber + "に" + pattern + "がありません");
		StringBuilder sb = new StringBuilder();
		sb.append(body.id(m.group()));
		while (m.find())
			sb.append("-").append(body.id(m.group()));
		return sb.toString();
	}

}
