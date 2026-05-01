package saka1029.shinryo.renderer;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

import saka1029.shinryo.common.TextWriter;
import saka1029.shinryo.parser.Node;
import saka1029.shinryo.parser.Pat;

public class 施設基準通知本文 extends 施設基準本文 {

    public 施設基準通知本文(String outDir) throws IOException {
        super(outDir, "t");
    }

    static final String 別添 = "別\\s*添\\s*"+ Pat.数字 + "\\s*の";
    static final String 及び = "\\s*(、|及\\s*び)";
    static final String 別紙 = "\\s*(別\\s*紙|様\\s*式)\\s*" + Pat.数字 + "\\s*(の\\s*" + Pat.数字 + ")*";
    static final Pattern ALL_PAT = Pattern.compile(別添 + 別紙 + "(" + 及び + 別紙 + ")*");
    static final Pattern SUB_PAT = Pattern.compile("(?<H>" + 別添 + ")(?<B0>" + 別紙 + ")|(?<C>" + 及び +")(?<B1>" + 別紙 + ")");

    static String anchor(String txt) {
        return "<a href='pdf/%s.pdf'>%s</a>".formatted(Pat.正規化(txt), txt);
    }
    static String anchor(String head, String txt) {
        return "<a href='pdf/%s.pdf'>%s</a>".formatted(Pat.正規化(head + txt), txt);
    }

    /**
     * 令和８年施設基準(通知) 特掲診療料の施設基準等 第９ 在宅療養支援診療所 ４届け出に関する事項
     * における以下の参照パターンに対応する。
     * <ul>
     * <li>別添２の様式 11 及び様式11の３</li>
     * <li>別添２の様式 11、様式11の３及び様式11 の５</li>
     * </ul>
     * この中の様式11、様式11の3、様式11の5へのリンクは
     * それぞれ先頭に「別添２」を付与する必要がある。
     */
	@Override
	public String anchor(String s, Node n) {
        String[] h = {null};
        return ALL_PAT.matcher(s).replaceAll(
            m -> SUB_PAT.matcher(m.group()).replaceAll(
                p -> p.group("H") != null
                    ? anchor((h[0] = p.group("H")) + p.group("B0"))
                    : p.group("C") + anchor(h[0], p.group("B1"))));
	}
	
    static final List<String> LINKS = List.of("第数字の");

    void node(Node node, int level, TextWriter writer) throws IOException {
        if (LINKS.contains(node.token.type.name) && !node.token.header.equals("削除"))
            link(node, level, writer, false);
        else
            text(node, level, writer);
    }
}
