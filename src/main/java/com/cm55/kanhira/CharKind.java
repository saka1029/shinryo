package com.cm55.kanhira;

/**
 * 文字種類の判定
 * @author admin
 */
public class CharKind {

  public static boolean isOkurigana(char c) {
    return 'a' <= c && c <= 'z';
  }
  
  /**
   * 文字列を構成する文字がすべてひらがなであるかを調べる。文字列長は0でもよい。
   * @param s
   * @return
   */
  public static boolean isHiragana(String s) {
    for (char c: s.toCharArray()) {
      if (!isHiragana(c)) return false;
    }
    return true;    
  }

//  /**
//   * 文字列を構成する文字がすべて漢字であるかを調べる。文字列長は0でも良い。
//   * @param s
//   * @return
//   */
//  public static boolean isKanji(String s) {
//    for (char c: s.toCharArray()) {
//      if (!isKanji(c)) return false;
//    }
//    return true;
//  }
//
//  public static boolean isKanjiOrHiragana(String s) {
//    for (char c: s.toCharArray()) {
//      Character.UnicodeBlock ub = unicodeBlock(c);
//      if (!isKanji(ub) && !isHiragana(ub)) return false;
//    }
//    return true;
//  }
  
  public static boolean isHiragana(int c) {
    if (c < 0) return false;
    return isHiragana((char)c);
  }
  
  /**
   * 文字がひらがなであるかを調べる。長音「ー」はひらがなとして認められる。
   * @param c
   * @return
   */
  public static boolean isHiragana(char c) {
    return isHiragana(unicodeBlock(c)) || c == 'ー';
  }

  /** 
   * 指定したユニコードブロックがひらがなであるかを調べる。長音は含まれない。
   * @param ub
   * @return
   */
  public static boolean isHiragana(Character.UnicodeBlock ub) {
    return ub.equals(Character.UnicodeBlock.HIRAGANA);
  }
  
  public static boolean isKatakana(int c) {
    if (c < 0) return false;
    return isKatakana((char)c);
  }
  
  /**
   * 文字がカタカナであるかを調べる。濁音・半濁音のブロックはひらがなであるが、カタカナとしても認める。
   * @param c
   * @return
   */
  public static boolean isKatakana(char c) {
    // 濁音・半濁音
    if (c == '゛' || c == '゜') return true;
    return isKatakana(unicodeBlock(c));
  }
  
  public static boolean isKatakana(Character.UnicodeBlock ub) {
    return ub.equals(Character.UnicodeBlock.KATAKANA);
  }

  /**
   * 文字が漢字であるかを調べる
   * @param c
   * @return
   */
  public static boolean isKanji(char c) {
    return isKanji(unicodeBlock(c));
  }

  public static boolean isKanji(Character.UnicodeBlock ub) {
    return ub.equals(Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS);
  }
  
  /**
   * 文字のユニコードブロックを取得する。
   * @param c
   * @return
   */
  public static Character.UnicodeBlock unicodeBlock(char c) {
    return Character.UnicodeBlock.of(c);
  }
}
