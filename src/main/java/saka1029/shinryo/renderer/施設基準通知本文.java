package saka1029.shinryo.renderer;

import java.io.IOException;
import java.util.Deque;
import java.util.List;
import java.util.regex.Pattern;

import saka1029.shinryo.common.TextWriter;
import saka1029.shinryo.parser.Node;
import saka1029.shinryo.parser.Pat;

public class 施設基準通知本文 extends 施設基準本文 {

    public 施設基準通知本文(String outDir) throws IOException {
        super(outDir, "t");
    }

	static final Pattern 様式名パターン = Pattern.compile(Pat.施設基準様式名パターン);
	
	@Override
	public String anchor(String s, Node n) {
	    return 様式名パターン.matcher(s).replaceAll(m -> {
	        String y = m.group(), a = Pat.正規化(y);
	        return "<a href='pdf/%s.pdf'>%s</a>".formatted(a, y);
	    });
	}
	
    static final List<String> LINKS = List.of("第数字の");

    void node(Node node, int level, TextWriter writer, Deque<Link> links) throws IOException {
        if (LINKS.contains(node.token.type.name) && !node.token.header.equals("削除"))
            link(node, level, writer, links, false);
        else
            text(node, level, writer, links);
    }
}
