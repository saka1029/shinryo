/*
 * $Id: KatakanaConverterImpl.java,v 1.2 2003/01/01 08:18:44 kawao Exp $
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
 * This class implements conversion methods that converts a Katakana word.
 * 
 * @author Kawao, Tomoyuki (kawao@kawao.com)
 * @version $Revision: 1.2 $ $Date: 2003/01/01 08:18:44 $
 */
public class KatakanaConverter implements Converter {

  /**
   * Converts the Katakana word into the Hiragana word.
   * 
   * @param input
   *          the input object.
   * @param output
   *          the output writer object.
   * @return false if no character is converted, otherwise true.
   * @exception IOException
   *              if an I/O error occurred.
   */
  public Optional<String>convert(Input input) {
    if (!CharKind.isKatakana(input.first())) {
      return Optional.empty();
    }
    StringBuilder output = new StringBuilder();
    while (true) {
      int ch = input.first();
      if ((ch >= 'ァ' && ch <= 'ン') || ch == 'ヽ'
          || ch == 'ヾ') {
        // from small 'a' to 'n' and iteration marks
        input.consume(1);
        output.append((char) (ch - 0x60));
      } else if (ch == 'ヴ') {
        input.consume(1);
        output.append('う');
        output.append('゛');
      } else if (CharKind.isKatakana(ch)) {
        input.consume(1);
        output.append((char) ch);
      } else {
        break;
      }
    }
    return Optional.of(output.toString());
  }
}
