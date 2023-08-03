package saka1029.shinryo.renderer;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

import saka1029.shinryo.common.TextWriter;
import saka1029.shinryo.parser.Node;
import saka1029.shinryo.parser.Pat;

public class 施設基準告示本文 extends 施設基準本文 {

    public 施設基準告示本文(String outDir) throws IOException {
        super(outDir, "k");
    }
    
    static final Pattern 別表参照 = Pattern.compile("別\\s*表\\s*第(" + Pat.漢数字の + ")");
    
    @Override
    public String anchor(String s, Node n) {
        String k = n.path.replaceFirst("_.*", "");
        return 別表参照.matcher(s).replaceAll(m -> {
            String b = m.group(), a = Pat.漢数字正規化(m.group(1));
            return "<a href='k%s_b_%s.html'>%s</a>".formatted(k, a, b);
        });
    }

    static final List<String> LINKS = List.of("第漢数字", "別表", "別表第");

    void node(Node node, int level, TextWriter writer) throws IOException {
        if (LINKS.contains(node.token.type.name))
            link(node, level, writer, false);
        else
            text(node, level, writer);
    }
}
