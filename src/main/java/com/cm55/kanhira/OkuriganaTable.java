package com.cm55.kanhira;

import java.util.*;

/**
 * あるひらがな文字が漢字の送り仮名として適当かどうかをチェックするためのテーブル。
 * 例えば、「悪く」の「く」という送り仮名が適当かどうかは、「悪」という熟語の送り仮名イニシャルとして「k」が
 * あるかないかで決まる。
 * @author admin
 */
public class OkuriganaTable {

  /**
   * チェック対象の文字が送り仮名として適当であるかを調べる。
   * @param okurigana 送り仮名マーク
   * @param target チェック対称の文字
   * @return true:適当、false:不適当
   */
  public static boolean check(char okurigana, char target) {
    
    // 送り仮名マークの対象とするひらがなリストを取得する。
    char[]targetChars = map.get(okurigana);
    if (targetChars == null) return false;

    // チェック、対象ひらがなは昇順なのでバイナリサーチ
    return Arrays.binarySearch(targetChars,  target) >= 0;
  }
  
  /** 送り仮名イニシャルと、それに対してゆるされるひらがな */
  static final Object[] TABLE = {
      'a', "ぁあぃいぅうぇえぉおっ",
      'b', "っばびぶべぼ",
      'c', "ちっ",
      'd', "だぢっづでど",
      'e', "ぁあぃいぅうぇえぉおっゎわゐゑ",
      'f', "っふ",
      'g', "がぎぐげごっ",
      'h', "っはひふへほ",
      'i', "ぁあぃいぅうぇえぉおっゎわゐゑ",
      'j', "ざじずぜぞっ",
      'k', "かきくけこっヵヶ",
      'l', "らりるれろ",
      'm', "まみむめも",
      'n', "なにぬねのん",
      'o', "ぁあぃいぅうぇえぉおっゎわゐゑ",
      'p', "っぱぴぷぺぽ",
      'r', "らりるれろ",
      's', "さしすせそっ",
      't', "たちっつてと",
      'u', "ぁあぃいぅうぇえぉおっゎわゐゑ",
      'w', "ぁあぃいぅうぇえぉおっゎわゐゑを",
      'y', "ゃやゅゆょよ",
      'z', "ざじずぜぞっ",
  };

  /** 送り仮名イニシャル・許可ひらがなマップ。ひらがな配列は昇順に並ぶ */
  private static final Map<Character, char[]> map = new HashMap<>();
  
  static {
    for (int i = 0; i < TABLE.length; i += 2) {
      char initial = (Character)TABLE[i + 0];
      String targets = (String)TABLE[i + 1];
      char[]targetChars = targets.toCharArray();
      Arrays.sort(targetChars);
      map.put(initial, targetChars);
    }
  }
}
