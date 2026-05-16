package saka1029.shinryo.renderer;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import saka1029.shinryo.common.TextWriter;
import saka1029.shinryo.parser.Node;
import saka1029.shinryo.parser.Pat;

public class 施設基準告示本文 extends 施設基準本文 {

    public 施設基準告示本文(String outDir) throws IOException {
        super(outDir, "k");
    }
    
    final Set<String> 別表PATH = new HashSet<>();
    static final Pattern 別表参照 = Pattern.compile("別\\s*表\\s*第\\s*(" + Pat.漢数字の + ")");
    static final Pattern ひとつ上の参照 = Pattern.compile("(.*)(の\\s*" + Pat.漢数字 + ")$");
    
    @Override
    public String anchor(String s, Node n) {
        // String k = n.path.replaceFirst("_.*", "");
        String kihonTokkei = n.path.substring(0, 1);    // 1:基本, 2=特掲
        return 別表参照.matcher(s).replaceAll(m -> {
            String all = m.group();
            String norm = Pat.漢数字正規化(m.group(1));
            String bpath = kihonTokkei + "_b_" + norm;
            boolean found = 別表PATH.contains(bpath);   // 辞書にあるかどうかを調べます。
            // System.out.printf("path=%s all=%s norm=%s bpath=%s found=%s%n", n.path, all, norm, bpath, found);
            if (found)
                return "<a href='k%s.html'>%s</a>".formatted(bpath, all);
            // マッチしなかった場合、末尾の「の漢数字」を除外してみます。
            Matcher m2 = ひとつ上の参照.matcher(all);
            if (m2.find()) {
                String match = m2.group(1);
                norm = Pat.漢数字正規化(match);
                String rest = m2.group(2);
                bpath = kihonTokkei + "_b_" + norm;
                found = 別表PATH.contains(bpath);
                // System.out.printf("  path=%s all=%s+%s norm=%s bpath=%s found=%s%n", n.path, match, rest, norm, bpath, found);
                if (found)
                    return "<a href='k%s.html'>%s</a>%s".formatted(bpath, match, rest);
            }
            return all;
        });
    }

    static final List<String> LINKS = List.of("第漢数字", "別表", "別表第");

    void node(Node node, int level, TextWriter writer) throws IOException {
        if (LINKS.contains(node.token.type.name))
            link(node, level, writer, false);
        else
            text(node, level, writer);
    }

    @Override
    public void render(Node root, String title, String outHtmlFile) throws IOException {
        // 別表のパスを辞書「別表PATH」に追加します。
        root.visit(n -> {
            if (n.parent != null && n.parent.token != null && n.parent.token.type.name.equals("別表")) {
                // System.out.printf("type=%s id=%s path=%s%n", n.token.type.name, n.id, n.path);
                別表PATH.add(n.path);
            }
        });
        super.render(root, title, outHtmlFile);
    }
}
