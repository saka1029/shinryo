package saka1029.shinryo.kuromoji;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.junit.Test;

import com.atilika.kuromoji.ipadic.Tokenizer;

import saka1029.shinryo.common.Common;
import saka1029.shinryo.common.Param;
import saka1029.shinryo.parser.Node;
import saka1029.shinryo.parser.Token;
import saka1029.shinryo.parser.医科告示読込;

public class TestInvertedIndex {

    static final Logger logger = Common.logger(TestInvertedIndex.class);
    
    static final Param param = Param.of("in", "debug/out", "06");
    static final Set<String> MAIN_NODES = Set.of("章", "部", "節", "款", "通則");

    static List<String> words(String text) {
        List<String> result = new ArrayList<>();
        Tokenizer tokenizer = new Tokenizer() ;
        List<com.atilika.kuromoji.ipadic.Token> tokens = tokenizer.tokenize(text.replaceAll("\\s+", ""));
        StringBuilder noun = new StringBuilder();
        for (com.atilika.kuromoji.ipadic.Token token : tokens)
            // if (token.getPartOfSpeechLevel1().equals("名詞") && !token.getPartOfSpeechLevel2().equals("接尾")) {
            if (token.getPartOfSpeechLevel1().equals("名詞")) {
                noun.append(token.getSurface());
            } else {
                if (noun.length() > 0) {
                    result.add(noun.toString());
                    noun.setLength(0);
                }
            }
        if (noun.length() > 0)
            result.add(noun.toString());
        return result;
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
            // out.accept("%s:%s %s".formatted(url, t.number, t.header + t.body.stream().collect(Collectors.joining())));
            List<String> words = words(t.header + t.body.stream().collect(Collectors.joining()));
            out.accept("%s:%s".formatted(url, words.stream().collect(Collectors.joining(" "))));
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
