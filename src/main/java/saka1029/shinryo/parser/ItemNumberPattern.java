package saka1029.shinryo.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ItemNumberPattern {
	public final NumberPattern body;
	public final String prefix, suffix, conj;
	public final Pattern pattern;
	
	ItemNumberPattern(String prefix, NumberPattern body, String suffix, String conj) {
		this.prefix = prefix;
		this.body = body;
		this.suffix = suffix;
		this.conj = conj;
		if (conj == null)
			this.pattern = Pattern.compile(
				"%1$s%2$s%3$s".formatted(prefix, body.body, suffix));
		else
			this.pattern = Pattern.compile(
				"%1$s%2$s(%4$s%2$s)*%3$s".formatted(prefix, body.body, suffix, conj));
	}
	
	public String id(String itemNumber) {
		Matcher m = body.pattern.matcher(itemNumber);
		if (!m.find())
			throw new RuntimeException(itemNumber + "に" + pattern + "がありません");
		StringBuilder sb = new StringBuilder();
		sb.append(body.id(m.group()));
		while (m.find())
			sb.append(".").append(body.id(m.group()));
		return sb.toString();
	}

}
