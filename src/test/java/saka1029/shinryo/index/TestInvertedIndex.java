package saka1029.shinryo.index;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.junit.Test;

import saka1029.shinryo.common.Common;
import saka1029.shinryo.common.Param;
import saka1029.shinryo.parser.Node;
import saka1029.shinryo.parser.Token;
import saka1029.shinryo.parser.医科告示読込;

public class TestInvertedIndex {

    static final Logger logger = Common.logger(TestInvertedIndex.class);
    
    static final Param param = Param.of("in", "debug/out", "06");
    static final Set<String> MAIN_NODES = Set.of("章", "部", "節", "款", "通則");
    static final Pattern WORD = Pattern.compile(
        "[0-9０-９]*"
        + "[A-Za-zＡ-Ｚａ-ｚ"
        + "\\p{InGreek}\\p{IsHan}\\p{IsKatakana}"
        + "\\uFF0D"        // 全角ハイフンマイナス
        + "\\u30FC"        // 全角長音
        + "\\u2014"        // EMダッシュ
        + "\\u2015"        // 全角のダッシュ
        + "\\u2212"        // 全角のマイナス
        + "]+"
        + "[0-9０-９]*"
        + "|[0-9,０-９]+点");

    static List<String> tokenize(String text) {
        return WORD.matcher(text).results()
            .map(m -> m.group())
            .filter(w -> w.length() >= 3)
            .filter(w -> !w.startsWith("区分番号"))
            .toList();
    }

    static void index(Node node, String url, Consumer<String> out) throws IOException {
        if (!node.isRoot()) {
            Token t = node.token;
            if (t.type.name.equals("区分番号")) {
                url = node.id + ".html";
                logger.info(url);
            } else if (MAIN_NODES.contains(t.type.name))
                url = node.path + ".html";
            // else 親のURLを継承
            List<String> words = tokenize(t.header + " " + t.body.stream().collect(Collectors.joining()));
            out.accept("%s:%s".formatted(url, words.stream().collect(Collectors.joining(", "))));
        }
        for (Node child : node.children)
            index(child, url, out);
    }

   @Test
    public void test医科告示読込() throws IOException {
        logger.info(Common.methodName());
        String inTxtFile = param.inFile("i", "txt/ke.txt");
        String outTxtFile = param.outDir("ik-index.txt");
        Path.of(outTxtFile).getParent().toFile().mkdirs();
        Node root = new 医科告示読込().parse(inTxtFile);
        try (PrintWriter w = new PrintWriter(outTxtFile)) {
            index(root, null, w::println);
        }
    }
}
