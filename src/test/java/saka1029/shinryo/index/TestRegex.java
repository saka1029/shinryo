package saka1029.shinryo.index;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.junit.Test;

import saka1029.shinryo.common.Common;

public class TestRegex {
    Logger logger = Common.logger(TestRegex.class);

    static final Pattern KANJI = Pattern.compile("\\p{IsHan}+");
    static final Pattern ZENKAKU_ALPHABET = Pattern.compile("[Ａ-Ｚａ-ｚ]+");
    static final Pattern ZENKAKU_HIRAGANA = Pattern.compile("\\p{IsHiragana}+");
    static final Pattern ZENKAKU_KATAKANA = Pattern.compile("\\p{IsKatakana}+");
    static final Pattern GREEK = Pattern.compile("\\p{InGreek}+");

    List<String> findAll(Pattern pattern, String text) {
        return pattern.matcher(text).results().map(m -> m.group()).toList();
    }

    @Test
    public void testRegex() {
        String text = "abc漢字あいうカキクＤＦＧ123ｘｙｚαβγ４５６";
        assertEquals(List.of("漢字"), findAll(KANJI, text));
        assertEquals(List.of("ＤＦＧ", "ｘｙｚ"), findAll(ZENKAKU_ALPHABET, text));
        assertEquals(List.of("あいう"), findAll(ZENKAKU_HIRAGANA, text));
        assertEquals(List.of("カキク"), findAll(ZENKAKU_KATAKANA, text));
        assertEquals(List.of("αβγ"), findAll(GREEK, text));
    }   

}
