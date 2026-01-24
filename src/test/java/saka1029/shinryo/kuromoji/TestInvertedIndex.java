package saka1029.shinryo.kuromoji;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.Logger;
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

    static void index(Node node, String url, Consumer<String> out) throws IOException {
        if (!node.isRoot()) {
            Token t = node.token;
            if (t.type.name.equals("区分番号"))
                url = node.id + ".html";
            else if (MAIN_NODES.contains(t.type.name))
                url = node.path + ".html";
            // else 親のURLを継承
            out.accept("%s:%s %s".formatted(url, t.number, t.header + t.body.stream().collect(Collectors.joining())));
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
