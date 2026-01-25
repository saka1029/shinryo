package saka1029.shinryo.kanhira;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.*;

import com.cm55.kanhira.Kanhira;
import com.cm55.kanhira.KanjiYomiMap;
import com.cm55.kanhira.KanwaDict;

public class KanjiYomiMapTest {

  @Test
  public void test() {
    KanjiYomiMap map = new KanjiYomiMap();
    map.add("悪名高", "あくめいたかi");
    map.add("悪名高", "あくめいたかk");
    map.add("悪代官", "あくだいかん");
    map.add("悪徳不動産屋",  "あくとくふどうさんや"); 
        
    assertEquals(
      "悪徳不動産屋,あくとくふどうさんや,,6\n" + 
      "悪名高,あくめいたか,i,4\n" + 
      "悪名高,あくめいたか,k,4\n" + 
      "悪代官,あくだいかん,,3", map.lookup('悪').get().toString());
    
    Kanhira kakasi = new Kanhira(new KanwaDict[] { map });
    assertEquals("あくめいたかくあくめいたかいあくだいかん",
        kakasi.convert("悪名高く悪名高い悪代官"));
    
    assertTrue(map.remove("悪名高", "あくめいたかk"));
    assertEquals(
        "悪徳不動産屋,あくとくふどうさんや,,6\n" + 
        "悪名高,あくめいたか,i,4\n" + 
        "悪代官,あくだいかん,,3", map.lookup('悪').get().toString());
    
    map.add("悪名", "あくめい");
    
    assertEquals("あくめいくあくめいたかいあくだいかん",
        kakasi.convert("悪名高く悪名高い悪代官"));
  }
  
  @Test
  public void test2() {
    
    KanjiYomiMap map0 = new KanjiYomiMap();
    map0.add("悪代官", "あくしろかん");
    
    KanjiYomiMap map1 = new KanjiYomiMap();
    map1.add("悪代官", "あくだいかん");
    map1.add("悪徳不動産",  "あくとくふどうさん"); 
    map1.add("不動産",  "ふどうさん"); 
        
    Kanhira kakasi = new Kanhira(new KanwaDict[] { map0, map1 });
    assertEquals("あくしろかんとあくとくふどうさん", kakasi.convert("悪代官と悪徳不動産"));

    map0.add("悪徳",  "いーぶる"); 
    assertEquals("あくしろかんといーぶるふどうさん", kakasi.convert("悪代官と悪徳不動産"));
  }

  @Test
  public void test3() {
    
    KanjiYomiMap map0 = new KanjiYomiMap();
    map0.add("悪徳",  "あくとくなふどうさんがいしゃ"); 
    
    KanjiYomiMap map1 = new KanjiYomiMap();
    map1.add("悪徳不動産",  "あくとくふどうさん"); 
        
    Kanhira kakasi = new Kanhira(new KanwaDict[] { map0, map1 });


    assertEquals("あくとくなふどうさんがいしゃ", kakasi.convert("悪徳不動産"));
  }
  
}
