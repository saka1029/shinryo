package com.cm55.kanhira;

import java.io.*;
import java.util.*;
import java.util.stream.*;

/**
 * Kakasi辞書を読み込み{@link KanjiYomiMap}を取得する。
 * 辞書エラーの場合は、メッセージを標準エラーに出力する。
 * <p>
 * 辞書中にある後の方が優先されることに注意。
 * </p>
 */
public class KakasiDictReader {

  private static final String DEFAULT_ENCODING = "JISAutoDetect";

  /** 指定ファイルから読み込む。エンコーディングは"JISAutoDetect"になる。 */
  public static KanjiYomiMap load(String filename) throws IOException {
    return load(filename, DEFAULT_ENCODING);
  }

  /** 指定ファイルからエンコーディングを指定して読み込む */
  public static KanjiYomiMap load(String filename, String encoding) throws IOException {
    try (InputStream in = new FileInputStream(filename)) {
      return load(in, encoding);
    }
  }
  
  /** 指定入力ストリームから読み込む。エンコーディングは"JISAutoDetect"になる */
  public static KanjiYomiMap load(InputStream in) throws IOException {
    return load(in,  DEFAULT_ENCODING);
  }
  
  /** 指定入力ストリームからエンコーディングを指定して読み込む */
  public static KanjiYomiMap load(InputStream in, String encoding) throws IOException {
    try (Reader reader = new InputStreamReader(in, encoding)) {      
      return load(reader);
    }    
  }

  /**
   * リーダーから読み込み、{@link KanjiYomiMap}を作成する。
   * ここではエラーがあっても標準エラー出力に表示し、処理打ち切りなどはしない。
   * @param reader
   * @return {@link KanjiYomiMap}
   * @throws IOException
   */
  public static KanjiYomiMap load(Reader reader) throws IOException {
    KanjiYomiMap map = new KanjiYomiMap();
    try (BufferedReader in = new BufferedReader(reader)) {
      while (true) {
        String line = in.readLine();
        if (line == null) break;
        if (line.length() == 0 || line.startsWith(";")) continue;
        String[]fields =
            Arrays.stream(line.split("[ ,\t]")).filter(s->s.length() > 0)
            .collect(Collectors.toList()).toArray(new String[0]);
        if (fields.length < 2) {
          System.err.println("KanwaDictionary: Ignored line: " + line);
          continue;
        }
        try {
          map.add(fields[1], fields[0]);
        } catch (Exception ex) {
          System.err.println("KanwaDictionary:" + ex.getMessage());
        }
      }
      return map;
    }    
  }
}
