package saka1029.shinryo.parser;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.Test;

public class TestTokenReader {

	static final PrintStream OUT = new PrintStream(System.out, true, StandardCharsets.UTF_8);

	static final TokenType 通則 = new TokenType("通則", "\\s*(?<N>通則)(?<H>)");
	static final TokenType 数字 = new TokenType("数字", "\\s*(?<N>[0-9０-９]+)\\s+(?<H>.*)");
	static final TokenType 節 = new TokenType("節", "\\s*(?<N>第[0-9０-９]節+)\\s+(?<H>.*)");
	static final TokenType 区分 = new TokenType("区分", "\\s*(?<N>区分)(?<H>)");
	static final TokenType 区分番号 = new TokenType("区分番号", "\\s*(?<N>[０-９]{2}(の[０-９]+)*)\\s+(?<H>.*)");
	static final TokenType カナ = new TokenType("カナ", "\\s*(?<N>[" + TokenType.イロハ + "])\\s+(?<H>.*)");
	static final TokenType 注１ = new TokenType("注１", "\\s*(?<N>注１)\\s+(?<H>.*)");
	static final TokenType 注 = new TokenType("注", "\\s*(?<N>注)\\s+(?<H>.*)");
	static final TokenType 括弧数字 = new TokenType("括弧数字", "\\s*(?<N>[(（][0-9０-９]+[)）])\\s+(?<H>.*)");

	static List<TokenType> types = List.of(通則, 区分番号, 数字, 節, 区分, カナ, 注１, 注, 括弧数字);

    @Test
    public void testRead() throws IOException {
		String inFile = "data/04-t-kokuji.txt";
		List<Token> tokens = TokenReader.read(types, inFile);
		for (Token t : tokens)
		    OUT.println(t);
		assertEquals(212, tokens.size());
		assertEquals(数字, tokens.get(211).type());
		assertEquals("２", tokens.get(211).number());
    }

}
