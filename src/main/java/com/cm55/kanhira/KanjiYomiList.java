package com.cm55.kanhira;

import java.util.*;
import java.util.stream.*;

/**
 * 最初の文字が同じ漢字の熟語について、その漢字の長さ順に{@link KanjiYomi}を格納したもの。
 * 例えば、最初が「悪」の場合には、
 * <ul>
 * <li>悪徳不動産屋
 * <li>悪天候時
 * <li>悪代官
 * </ul>
 * の順序で格納されている。
 * つまり、最長一致の方針になっている。
 * 例えば対象文字列が「悪い」であった場合、「わる」よりも「わるい」が先に一致する。
 */
public class KanjiYomiList  {

  /**
   * この{@link TreeSet}は{@link KanjiYomi#compareTo(KanjiYomi)}によって順序付けられる。
   * {@link KanjiYomi}はその{@link KanjiYomi#wholeLength()}の逆順に順序付けされる。
   */
  private TreeSet<KanjiYomi>list = new TreeSet<>();
  
  /** {@link KanjiYomi}を追加する */
  public synchronized void add(KanjiYomi kanjiYomi) {
    list.add(kanjiYomi);

  }
  
  /** {@link KanjiYomi}を削除する */
  public synchronized boolean remove(KanjiYomi kanjiYomi) {
    return list.remove(kanjiYomi);    
  }

  /** 登録数を取得する */
  public synchronized int size() {
    return list.size();
  }

  /**
   * このリストに含まれる{@link KanjiYomi}の{@link KanjiYomi#wholeLength()}のうちの最大長を返す。
   * @return
   */
  public synchronized int maxWholeLength() {
    return list.first().wholeLength();
  }

  /** 処理用のストリームを取得 */
  public synchronized Stream<KanjiYomi>stream() {
    return list.stream();
  }
  
  /**
   * デバッグ用。文字列化
   */
  @Override
  public synchronized String toString() {
    return list.stream().map(s->s.toString()).collect(Collectors.joining("\n"));
  }
}