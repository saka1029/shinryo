package com.cm55.kanhira;

import java.util.*;

/**
 * 漢字かな辞書インターフェース
 * @author admin
 */
public interface KanwaDict {

  /**
   * 漢字熟語の最初の一文字を入力し、その漢字から始まるすべての熟語情報を得る。
   * @param k　漢字熟語の最初の一文字
   * @return すべての熟語情報
   */
  public Optional<KanjiYomiList> lookup(char k);
}