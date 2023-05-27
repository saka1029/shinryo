package saka1029.shinryo.parser0;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
	    String id = itemPattern.id(from);
	    if (!to.equals(""))
	        id += "+" + itemPattern.id(to);
	    return new Token(this, number, header, id);
	}

	@Override
	public String toString() {
		return name;
	}
	
	/**
	 * id1の次にid2が続く場合はtrue、そうでない場合はfalseを返します。
	 * trueを返す例は以下のとおりです。
	 * id1が「1」のとき、id2が「2」または「1-2」
	 * id1が「1-2」のとき、id2が「2」または「1-3」または「1-2-2」
	 * id1が「1-2-3」のとき、id2が「2」または「1-3」または「1-2-4」または「1-2-3-2」
	 */
	public static boolean isNext(String id1, String id2) {
		List<Integer> ints = Stream.of(id1.split("-"))
			.map(e -> Integer.parseInt(e))
			.toList();
		for (int i = 0, size = ints.size(); i < size; ++i) {
			String next = ints.subList(0, i).stream()
				.map(e -> "" + e)
				.collect(Collectors.joining("-"))
				+ (i == 0 ? "" : "-") + (ints.get(i) + 1);
			if (id2.equals(next))
				return true;
		}
		return id2.equals(id1 + "-2");
	}
	
}
