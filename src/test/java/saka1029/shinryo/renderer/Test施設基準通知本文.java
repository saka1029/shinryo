package saka1029.shinryo.renderer;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.junit.Test;

import saka1029.shinryo.common.Common;
import saka1029.shinryo.common.Param;
import saka1029.shinryo.parser.Node;
import saka1029.shinryo.parser.Parser;
import saka1029.shinryo.parser.Pat;
import saka1029.shinryo.parser.施設基準通知読込;

public class Test施設基準通知本文 {

    static final Logger logger = Common.logger(Test施設基準通知本文.class);
    
    static final Param param = Param.of("in", "debug/html", "02");

//    @Test
    public void testRender() throws IOException {
        logger.info(Common.methodName());
        String 点数表 = "k";
        String kTxt = param.inFile(点数表, "txt/te.txt");
        String outDir = param.outDir(点数表);
        String title = param.title(点数表) + "(通知)";
        String outHtmlFile = "tuti.html";
        Common.copyTree(param.inHomeDir(), param.outHomeDir());
        if (Files.exists(Path.of(param.inDir(点数表, "img"))))
            Common.copyTree(param.inDir(点数表, "img"), param.outDir(点数表, "img"));
        Node kRoot = Parser.parse(new 施設基準通知読込(), false, kTxt);
        new 施設基準通知本文(outDir).render(kRoot, title, outHtmlFile);
    }

//     static final Pattern 施設基準様式 = 施設基準通知本文.施設基準様式名パターン;
//     static String y(String s) {
//         Matcher m = 施設基準様式.matcher(s);
//         if (!m.find())
//             return null;
//         return Pat.正規化(m.group());
//     }

//     @Test
//     public void test施設基準様式名パターン() {
//         assertEquals("T3-S4", y("別添３の別紙４"));
// //        assertEquals("T7", y("別添７"));
//         assertEquals("T6-S1-2-3", y("別 添６ の 別紙１の２の３"));
//         assertEquals("T7-Y1-2-3", y("別添 ７ の 様式 １ の ２ の ３"));
//         assertEquals("T2-Y1", y("別添2の様式１"));
//     }

    static final String 別添 = "別\\s*添\\s*"+ Pat.数字 + "\\s*の";
    static final String 及び = "\\s*(、|及\\s*び)";
    static final String 別紙 = "\\s*(別\\s*紙|様\\s*式)\\s*" + Pat.数字 + "\\s*(の\\s*" + Pat.数字 + ")*";
    static final Pattern ALL_PAT = Pattern.compile(別添 + 別紙 + "(" + 及び + 別紙 + ")*");
    static final Pattern SUB_PAT = Pattern.compile("(?<H>" + 別添 + ")(?<B0>" + 別紙 + ")|(?<C>" + 及び +")(?<B1>" + 別紙 + ")");
    static final String BUN =
    "１の(１)及び(２)の在宅療養支援診療所の施設基準に係る届出は、別添２の様式 11 及び様式11の３を用いること。"
    + "１の(３)の在宅療養支援診療所の施設基準に係る届出は、別添２の様式11を用いること。"
    + "２の(３)の在宅医療充実体制加算の施設基準に係る届出は、別添２の様式 11及び様式 11 の３を用いること。"
    + "２の(４)の在宅療養実績加算１及び２の(５)の在宅療養実績加算２の施設基準に係る届出は、"
    + "別添２の様式11 及び様式 11の５を用いること。"
    + "なお、在宅療養支援診療所の施設基準に係る届出と在宅医療充実体制加算、在宅療養実績加算１又は在宅療養実績加算２"
    + "を併せて届け出る場合であって、別添２の様式 11、様式11の３及び様式11 の５を用いる場合は、"
    + "それぞれ１部のみの届出で差し支えない。";
    static final String EXPECT =
    "１の(１)及び(２)の在宅療養支援診療所の施設基準に係る届出は、<a href='pdf/T2-Y11.pdf'>別添２の様式 11 </a>及び<a href='pdf/T2-Y11-3.pdf'>様式11の３</a>を用いること。"
    + "１の(３)の在宅療養支援診療所の施設基準に係る届出は、<a href='pdf/T2-Y11.pdf'>別添２の様式11</a>を用いること。"
    + "２の(３)の在宅医療充実体制加算の施設基準に係る届出は、<a href='pdf/T2-Y11.pdf'>別添２の様式 11</a>及び<a href='pdf/T2-Y11-3.pdf'>様式 11 の３</a>を用いること。"
    + "２の(４)の在宅療養実績加算１及び２の(５)の在宅療養実績加算２の施設基準に係る届出は、<a href='pdf/T2-Y11.pdf'>"
    + "別添２の様式11 </a>及び<a href='pdf/T2-Y11-5.pdf'>様式 11の５</a>を用いること。"
    + "なお、在宅療養支援診療所の施設基準に係る届出と在宅医療充実体制加算、在宅療養実績加算１又は在宅療養実績加算２"
    + "を併せて届け出る場合であって、<a href='pdf/T2-Y11.pdf'>別添２の様式 11</a>、<a href='pdf/T2-Y11-3.pdf'>様式11の３</a>及び<a href='pdf/T2-Y11-5.pdf'>様式11 の５</a>を用いる場合は、"
    + "それぞれ１部のみの届出で差し支えない。";

    static String anchor(String txt) {
        return "<a href='pdf/%s.pdf'>%s</a>".formatted(Pat.正規化(txt), txt);
    }
    static String anchor(String head, String txt) {
        return "<a href='pdf/%s.pdf'>%s</a>".formatted(Pat.正規化(head + txt), txt);
    }

    static String 施設基準様式変換(String s, Pattern all, Pattern sub) {
        String[] h = {null};
        return all.matcher(s).replaceAll(
            m -> sub.matcher(m.group()).replaceAll(
                n -> n.group("H") != null
                    ? anchor((h[0] = n.group("H")) + n.group("B0"))
                    : n.group("C") + anchor(h[0], n.group("B1"))));
    }

    @Test
    public void test施設基準様式変換() {
        assertEquals(EXPECT, 施設基準様式変換(BUN, ALL_PAT, SUB_PAT));
    }
    
}
