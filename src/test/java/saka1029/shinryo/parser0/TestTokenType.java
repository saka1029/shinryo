package saka1029.shinryo.parser0;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import saka1029.shinryo.parser0.ItemPattern;
import saka1029.shinryo.parser0.NumberPattern;
import saka1029.shinryo.parser0.Token;
import saka1029.shinryo.parser0.TokenType;

public class TestTokenType {

	static final TokenType 別表第漢数字の = TokenType.simple("別表第漢数字の", ItemPattern.conj("別表第", NumberPattern.漢数字, "", "の"));
	static final TokenType 数字の = TokenType.fromTo("数字の", ItemPattern.conj("", NumberPattern.数字, "", "の"));
	static final TokenType 漢数字の = TokenType.fromTo("漢数字の", ItemPattern.conj("", NumberPattern.漢数字, "", "の"));
	static final TokenType 括弧カナ = TokenType.fromTo("括弧カナ", ItemPattern.simple("", NumberPattern.括弧カナ, ""));

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

    @Test
    public void testMatchカナからまで() {
        String line = "(イ)及び(ウ) 削除";
        Token token = 括弧カナ.match(line);
        assertNotNull(token);
        assertEquals("(イ)及び(ウ)", token.number);
        assertEquals("削除", token.header);
        assertEquals("2+3", token.id);
    }

    @Test
    public void testIsNext() {
    	assertTrue(TokenType.isNext("1", "2"));
    	assertTrue(TokenType.isNext("1", "1-2"));
    	assertTrue(TokenType.isNext("1-2", "2"));
    	assertTrue(TokenType.isNext("1-2", "1-3"));
    	assertTrue(TokenType.isNext("1-2", "1-2-2"));
    	assertTrue(TokenType.isNext("1-2-3", "2"));
    	assertTrue(TokenType.isNext("1-2-3", "1-3"));
    	assertTrue(TokenType.isNext("1-2-3", "1-2-4"));
    	assertTrue(TokenType.isNext("1-2-3", "1-2-3-2"));
    }
}
