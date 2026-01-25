/*
 * $Id: Converter.java,v 1.3 2003/01/01 08:18:44 kawao Exp $
 *
 * KAKASI/JAVA
 *  Copyright (C) 2002-2003  KAWAO, Tomoyuki (kawao@kawao.com)
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package com.cm55.kanhira;

import java.io.*;
import java.util.*;

/**
 * An object that implements the Converter interface can convert a string.
 * 
 * @author Kawao, Tomoyuki (kawao@kawao.com)
 * @version $Revision: 1.3 $ $Date: 2003/01/01 08:18:44 $
 */
public interface Converter {

  /**
   * Converts the string form the specified input object and output the result
   * to the specified writer.
   *
   * @param input
   *          the input object.
   * @param output
   *          the output writer object.
   * @return false if no character is converted, otherwise true.
   * @exception IOException
   *              if an I/O error occurred.
   */
  Optional<String>convert(Input input);

  /**
   * 文字列入力インターフェース
   * <p>
   * {@link #read()},{@link #read(int)}で文字列を読み込んでいくが、
   * 読み込まれたものはバッファ上に保持されており、いくらでも再読込が可能になる。
   * ただし、バッファ内にあるものについて、{@link #consume(int)}で消費する、つまり捨てることにより、
   * その文字列については二度と読み込まれないようにする。
   * </p>
   * 
   * @author admin
   */
  public static class Input {

    /** 読み込み元となるリーダ */
    private final Reader reader;

    /** 読み込みバッファ */
    private final StringBuffer buffer = new StringBuffer();

    /** buffer中の次の読み込み位置 */
    private int readPosition;

    /**
     * 読み込み元リーダを指定する
     */
    public Input(Reader reader) {
      this.reader = reader;
      buffer.setLength(0);
    }

    /**
     * 現在のポインタから一文字を読み込んで返し、ポインタを進める。無い場合は-1を返す。
     * 
     * @return 読み込んだ文字、あるいは-1
     */
    public int read() {
      String s = read(1);
      if (s.length() == 0)
        return -1;
      return s.charAt(0);
    }

    /**
     * 現在のポインタから指定数の文字を読み込み、文字列として返す。 返される文字列は0から指定数までの長さになる。その長さ分ポインタを進める。
     * 
     * @param maxLen
     *          読み込み希望文字数
     * @return 実際に読み込まれた文字列
     */
    public String read(int maxLen) {
      StringBuilder output = new StringBuilder();
      for (; output.length() < maxLen; readPosition++) {
        if (buffer.length() <= readPosition) {
          int ch;
          try {
            ch = reader.read();
          } catch (IOException ex) {
            throw new RuntimeException(ex);
          }
          if (ch < 0) {
            break;
          }
          buffer.append((char) ch);
        }
        char ch = buffer.charAt(readPosition);
        output.append(ch);
      }
      return output.toString();
    }

    /**
     * 現在のポインタをトップに戻す。 ※「トップ」の意味は{@link #consume(int)}を参照のこと。
     */
    public void reset() {
      readPosition = 0;
    }

    /**
     * 現在のポインタをトップに戻し、一文字を読み込み、ポインタを1進める。 つまり、reset()とread()を行うに等しい。
     * ※「トップ」の意味は{@link #consume(int)}を参照のこと。
     * 
     * @return 読み込んだ文字、あるいは-1
     */
    public int first() {
      reset();
      return read();
    }

    /**
     * 指定長の文字列を「消費」し、二度と読み込まれないようにする。 ただし、read()にて既に読み込まれ、バッファ内にある文字についてのみ行われる。
     * それ以上の長さを指定しても超過分は無視される。 消費された文字列の次の文字が「トップ」になる。
     * これはバッファ内に残っている場合もあるし、{@link #reader}から読み込まなければならない場合もある。
     * 
     * @param length
     *          消費する文字数
     */
    public void consume(int length) {
      buffer.delete(0, length);
      readPosition = 0;
    }
  }

}
