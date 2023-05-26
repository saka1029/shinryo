package saka1029.shinryo.parser;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

public class TestSimpleParser {
	
	static final PrintStream OUT = new PrintStream(System.out, true, StandardCharsets.UTF_8);
	
	static record Token(TokenType type, String number, String header) { 
		@Override
		public String toString() {
			return "Token(%s, %s, %s)".formatted(type.name, number, header);
		}
	}

	static class TokenType {
		final String name;
		final Pattern pattern;
		TokenType(String name, String pattern) {
			this.name = name;
			this.pattern = Pattern.compile(pattern);
		}
		
		Token match(String line) {
			Matcher m = pattern.matcher(line);
			if (!m.matches())
				return null;
			return new Token(this, m.group("N"), m.group("H"));
		}
	}
	
	static final String イロハ =
	    "イロハニホヘトチリヌルヲ"
	    + "ワカヨタレソツネナラム"
	    + "ウヰノオクヤマケフコエテ"
	    + "アサキユメミシヱヒモセスン";
	
	static final Pattern COMMENT = Pattern.compile("\\s*#.*");

	static final TokenType EOT = new TokenType("EOT", "\\s*(?<N>)(?<H>)");
	static final TokenType 通則 = new TokenType("通則", "\\s*(?<N>通則)(?<H>)");
	static final TokenType 数字 = new TokenType("数字", "\\s*(?<N>[0-9０-９]+)\\s+(?<H>.*)");
	static final TokenType 節 = new TokenType("節", "\\s*(?<N>第[0-9０-９]節+)\\s+(?<H>.*)");
	static final TokenType 区分 = new TokenType("区分", "\\s*(?<N>区分)(?<H>)");
	static final TokenType 区分番号 = new TokenType("区分番号", "\\s*(?<N>[０-９]{2}(の[０-９]+)*)\\s+(?<H>.*)");
	static final TokenType カナ = new TokenType("カナ", "\\s*(?<N>[" + イロハ + "])\\s+(?<H>.*)");
	static final TokenType 注１ = new TokenType("注１", "\\s*(?<N>注１)\\s+(?<H>.*)");
	static final TokenType 注 = new TokenType("注", "\\s*(?<N>注)\\s+(?<H>.*)");
	static final TokenType 括弧数字 = new TokenType("括弧数字", "\\s*(?<N>[(（][0-9０-９]+[)）])\\s+(?<H>.*)");

	static List<TokenType> types = List.of(
		通則,
		区分番号,
		数字,
		節,
		区分,
		カナ,
		注１,
		注,
		括弧数字
	);

	@Test
	public void test() throws IOException {
		String inFile = "data/04-t-kokuji.txt";
		List<String> lines = Files.readAllLines(Path.of(inFile));
		for (String line : lines) {
			if (COMMENT.matcher(line).matches())
				continue;
			for (TokenType tt : types) {
				Token t = tt.match(line);
				if (t != null) {
					OUT.println(t);
					break;
				}
			}
		}
	}

}
