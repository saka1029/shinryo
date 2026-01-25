package saka1029.shinryo.kanhira;

import java.util.*;

import org.junit.*;

public class KakasiDictReaderTest {

  @Test
  public void test() {
    String[]splited = "a  b \tc  ".split("[ ,\t]");
    Arrays.stream(splited).forEach(System.out::println);
    System.out.println(splited.length);
  }

}
