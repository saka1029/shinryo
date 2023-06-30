package saka1029.shinryo.renderer;

import java.util.logging.Logger;

import saka1029.shinryo.common.Trie;
import saka1029.shinryo.parser.Node;

public class 施設基準通知辞書 {
    
    static final Logger LOGGER = Logger.getLogger(施設基準通知辞書.class.getName());

    public static Trie<Node> create(Node tRoot) {
        Trie<Node> trie = new Trie<>();
        tRoot.visit(n -> {
            if (n.token == null || !n.token.type.name.equals("第数字の"))
                return;
            if (n.token.header.contains("経過措置"))
                return;
            String word = n.token.header;
            word = word.replaceAll("(の|に規定する|に関する)施設基準等?$", "");
            word = word.replaceAll("届出.*$", "");
            String[] ws = word.split("及び|、");
            for (String w : ws) {
                if (w.isBlank() || w.equals("削除"))
                    continue;
//                LOGGER.info(w);
                trie.put(w, n);
            }
        });
        return trie;
    }
}
