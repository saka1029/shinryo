package saka1029.shinryo.renderer;

import java.io.IOException;
import java.util.Deque;
import java.util.List;

import saka1029.shinryo.common.TextWriter;
import saka1029.shinryo.parser.Node;

public class 施設基準告示本文 extends 施設基準本文 {

    public 施設基準告示本文(String outDir) throws IOException {
        super(outDir, "k");
    }

    static final List<String> LINKS = List.of("第漢数字", "別表", "別表第");

    void node(Node node, int level, TextWriter writer, Deque<Link> links) throws IOException {
        if (LINKS.contains(node.token.type.name))
            link(node, level, writer, links, false);
        else
            text(node, level, writer, links);
    }
}
