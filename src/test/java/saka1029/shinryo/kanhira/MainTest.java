package saka1029.shinryo.kanhira;

import java.io.*;

import org.junit.*;

import com.cm55.kanhira.KakasiDictReader;
import com.cm55.kanhira.Kanhira;
import com.cm55.kanhira.KanwaDict;

import static org.junit.Assert.*;

public class MainTest {

  @Test
  public void test() throws IOException  {
    // KanwaDict dict = KakasiDictReader.load("dict/kakasidict");
    KanwaDict dict = KakasiDictReader.load("dict/kakasidict.utf8.txt", "UTF-8");
    
    Kanhira kakasi = new Kanhira(new KanwaDict[] { dict });
    
    assertEquals(
        "かいがいでのかいにゅうをしゅうりょうし、あめりかぐんをくににきかんさせるというかのせんきょこうやくを、ありがちなごかいがそんざいしているようにみえる。",
        kakasi.convert("海外での介入を終了し、アメリカ軍を国に帰還させるという彼の選挙公約を、ありがちな誤解が存在しているように見える。"));
    assertEquals(
        "よーろっぱぜんど、また、よーろっぱとのかんけいをつうじてほかのたいりくにも、われわれはそうらんとこんらんとてきがいしんをおこさなければならない",
        kakasi.convert("ヨーロッパ全土、また、ヨーロッパとの関係を通じて他の大陸にも、われわれは騒乱と混乱と敵愾心を起こさなければならない"));
    assertEquals("あんしんあんぜんなやふおく!じつげんにむけたYahoo!かんたんけっさいのしようへんこうについて（さいけい）",
        kakasi.convert( "安心安全なヤフオク!実現に向けたYahoo!かんたん決済の仕様変更について（再掲）"));
    assertEquals("あくめいたかいあくだいかんのわるだくみによって、きしゃのきしゃがきしゃできしゃした",
        kakasi.convert("悪名高い悪代官の悪巧みによって、貴社の記者が汽車で帰社した"));

    // ※送り仮名は全角ひらがなであること
    assertEquals("あくこうみによって", kakasi.convert("悪巧ミによって"));
    
    // 異体字
    assertEquals("てつお", kakasi.convert("鐡男"));
  }
}
