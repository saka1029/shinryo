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

    static String 施設基準様式変換(String s, Pattern all, Pattern sub) {
        String[] h = {null};
        return all.matcher(s).replaceAll(
            m -> sub.matcher(m.group()).replaceAll(
                n -> n.group("H") != null
                    ? anchor((h[0] = n.group("H")) + n.group("B0"))
                    : n.group("C") + anchor(h[0], n.group("B1"))));
    }
	
	@Override
	public String anchor(String s, Node n) {
	    return 施設基準様式変換(s, ALL_PAT, SUB_PAT);
	}
	
    static final List<String> LINKS = List.of("第数字の");

    void node(Node node, int level, TextWriter writer) throws IOException {
        if (LINKS.contains(node.token.type.name) && !node.token.header.equals("削除"))
            link(node, level, writer, false);
        else
            text(node, level, writer);
    }
}
