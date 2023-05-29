package saka1029.shinryo.parser;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.Test;

public class TestTokenReader {

	static final PrintStream OUT = new PrintStream(System.out, true, StandardCharsets.UTF_8);

    @Test
    public void testRead() throws IOException {
		String inFile = "data/04tk.txt";
		List<Token> tokens = TokenReader.read(TKParser.TYPES, inFile);
//		for (Token t : tokens)
//		    OUT.println(t);
		assertEquals(214, tokens.size());
		assertEquals(TKParser.数字, tokens.get(213).type());
		assertEquals("２", tokens.get(213).number());
    }

}
