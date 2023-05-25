package saka1029.shinryo.parser;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestTokenType {

	static final TokenType 別表第漢数字の = TokenType.simple("別表第漢数字の", ItemPattern.conj("別表第", NumberPattern.漢数字, "", "の"));
	static final TokenType 数字の = TokenType.fromTo("数字の", ItemPattern.conj("", NumberPattern.数字, "", "の"));
	static final TokenType 漢数字の = TokenType.fromTo("漢数字の", ItemPattern.conj("", NumberPattern.漢数字, "", "の"));

    @Test
    public void testMatchの() {
        String line = "別表第三の一の二 療養・就労両立支援指導料の注１に規定する疾患";
        Token token = 別表第漢数字の.match(line);
        assertNotNull(token);
        assertEquals("別表第三の一の二", token.number);
        assertEquals("療養・就労両立支援指導料の注１に規定する疾患", token.header);
        assertEquals("3-1-2", token.id);
    }

    @Test
    public void testMatchからまで() {
        String line = "４の２から４の１２の３まで 削除";
        Token token = 数字の.match(line);
        assertNotNull(token);
        assertEquals("４の２から４の１２の３まで", token.number);
        assertEquals("削除", token.header);
        assertEquals("4-2+4-12-3", token.id);
    }

    @Test
    public void testMatch漢数字からまで() {
        String line = "四の二から四の十二の三まで 削除";
        Token token = 漢数字の.match(line);
        assertNotNull(token);
        assertEquals("四の二から四の十二の三まで", token.number);
        assertEquals("削除", token.header);
        assertEquals("4-2+4-12-3", token.id);
    }

}
