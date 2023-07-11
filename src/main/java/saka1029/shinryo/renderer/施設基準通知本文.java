package saka1029.shinryo.renderer;

import java.io.IOException;
import java.util.Deque;
import java.util.List;

import saka1029.shinryo.common.TextWriter;
import saka1029.shinryo.parser.Node;

public class 施設基準通知本文 extends 施設基準本文 {

    public 施設基準通知本文(String outDir) throws IOException {
        super(outDir, "t");
    }

    static final List<String> LINKS = List.of("第数字の", "別添");

    void node(Node node, int level, TextWriter writer, Deque<Link> links) throws IOException {
        if (LINKS.contains(node.token.type.name) && !node.token.header.equals("削除"))
            link(node, level, writer, links);
        else
            text(node, level, writer, links);
    }
}
