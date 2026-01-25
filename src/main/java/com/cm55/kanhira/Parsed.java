package com.cm55.kanhira;

import java.util.*;

/**
 * 漢字・ひらがなペアを読み込みパースし、{@link KanjiYomi}作成に必要なデータ、
 * {@link KanjiYomiMap}登録に必要なデータを取得する。
 * 漢字でない、かなではない等のエラー時には{@link IllegalArgumentException}が発生する。
 * @author ysugimura
 */
class Parsed {

  /** 漢字一文字目 */
  final char key;
  
  /** 漢字全体 */
  final String kanji;
  
  /** よみ */
  final String yomi;
  
  /** 送り仮名 */
  final Optional<Character>okurigana;
  
  Parsed(String kanjiInput, String yomiInput) {

    // 漢字入力の一文字目のみを漢字コードか調べる、二文字目以降は漢字でなくてもよい。「貴ノ花」など。
    Character.UnicodeBlock kanjiBlock = Character.UnicodeBlock.of(kanjiInput.charAt(0));
    if (!kanjiBlock.equals(Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS)) {
      throw new IllegalArgumentException("First character not Kanji:" + kanjiInput);
    }
    
    // 異体字を普通字に変換する。比較の時もあらかじめ異体字が普通字に変換され比較される。    
    kanji = ItaijiTable.convert(kanjiInput);
    key = kanji.charAt(0);

    int yomiLength = yomiInput.length();
    char yomiLast = yomiInput.charAt(yomiLength - 1);
    if (CharKind.isOkurigana(yomiLast)) {
      okurigana = Optional.of(yomiLast);
      yomi = convertYomi(yomiInput.substring(0, yomiLength - 1));
    } else {
      yomi = convertYomi(yomiInput);
      okurigana = Optional.empty();  
    }
  }

  String convertYomi(String yomi) {
    int yomiLength = yomi.length();
    StringBuffer yomiBuffer = new StringBuffer(yomiLength);
    
    for (char ch: yomi.toCharArray()) {
      Character.UnicodeBlock block = Character.UnicodeBlock.of(ch);
      if (!block.equals(Character.UnicodeBlock.HIRAGANA) && !block.equals(Character.UnicodeBlock.KATAKANA)) {
        throw new IllegalArgumentException("Not Hiragana or Katakana:" + yomi);
      }
      if ((ch >= '\u30a1' && ch <= '\u30f3') || ch == '\u30fd' || ch == '\u30fe') {
        yomiBuffer.append((char) (ch - 0x60));
      } else if (ch == '\u30f4') { // 'vu'
        yomiBuffer.append('\u3046');
        yomiBuffer.append('\u309b');
      } else {
        yomiBuffer.append(ch);
      }
    }
    return yomiBuffer.toString();    
  }
}
