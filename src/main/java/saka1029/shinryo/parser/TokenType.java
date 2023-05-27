package saka1029.shinryo.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record TokenType(
    String name,
    Pattern pattern) {

    public static final TokenType ROOT = new TokenType("ROOT", "");
    public static final TokenType START = new TokenType("START", "");
    
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

    public TokenType(String name, String pattern) {
        this(name, Pattern.compile(pattern));
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
}
