package saka1029.shinryo.parser;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record TokenType(
    String name,
    Pattern pattern,
    Function<String, String> identifier) {

    public static final TokenType ROOT = new TokenType("ROOT", "", Pat.固定値id("ROOT"));
    public static final TokenType START = new TokenType("START", "", Pat.固定値id("START"));
    
	static final String アイウ =
	    "アイウエオカキクケコ"
	    + "サシスセソタチツテト"
	    + "ナニヌネノハヒフヘホ"
	    + "マミムメモヤユヨ"
	    + "ラリルレロワヰヱヲン";

	public static final String イロハ =
	    "イロハニホヘトチリヌルヲ"
	    + "ワカヨタレソツネナラム"
	    + "ウヰノオクヤマケフコエテ"
	    + "アサキユメミシヱヒモセスン";

    public TokenType(String name, String pattern, Function<String, String> identifier) {
        this(name, Pattern.compile(pattern), identifier);
    }
    
    public Token match(String line, String fileName, int pageNo, int lineNo) {
        Matcher matcher = pattern.matcher(line);
        if (!matcher.matches())
            return null;
        int i = 0, length = line.length();
        while (i < length && line.charAt(i) == ' ')
            ++i;
        return new Token(this, matcher.group("N"), matcher.group("H"), fileName, pageNo, lineNo, i);
    }
    
    public String id(String number) {
        return identifier.apply(number);
    }
}
