package saka1029.shinryo.parser;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.junit.Test;

import saka1029.shinryo.common.Logging;

public class TestParser {

    static { Logging.init(); } 
    static final Logger logger = Logger.getLogger(TestParser.class.getSimpleName());
    
    static final PrintStream OUT = new PrintStream(System.out, true, StandardCharsets.UTF_8);

    @Test
    public void test調剤告示読み込み() throws IOException {
        String inTxtFile = "in/04/t/txt/ke.txt";
        String outTxtFile = "debug/out/04/t/k-tree.txt";
        Node root = new 調剤告示読み込み().parse(inTxtFile);
        root.summary(outTxtFile);
    }

    @Test
    public void test調剤通知読み込み() throws IOException {
        String inTxtFile = "in/04/t/txt/te.txt";
        String outTxtFile = "debug/out/04/t/t-tree.txt";
        Node root = new 調剤通知読み込み().parse(inTxtFile);
        root.summary(outTxtFile);
    }

    @Test
    public void test医科告示読み込み() throws IOException {
        String inTxtFile = "in/04/i/txt/ke.txt";
        String outTxtFile = "debug/out/04/i/k-tree.txt";
        Node root = new 医科告示読み込み().parse(inTxtFile);
        root.summary(outTxtFile);
    }
    
    static void makeUniqId(Node node) {
    	// 子のidで子をグループ化します。
    	Map<String, List<Node>> map = node.children.stream()
    		.collect(Collectors.groupingBy(n -> n.id));
    	// 同一idの子のidに連番を付与してユニークにします。
    	for (Entry<String, List<Node>> e : map.entrySet()) {
    		if (e.getValue().size() <= 1)
    			continue;
    		List<Node> list = e.getValue();
    		for (int i = 0, size = list.size(); i < size; ++i)
    			list.get(i).id += i;
    	}
    	for (Node child : node.children)
    		makeUniqId(child);
    }
    
    static void makeUniqPath(Node node) {
    	for (Node child : node.children) {
    		child.path = node.isRoot() ? child.id : node.path + "_" + child.id;
    		makeUniqPath(child);
    	}
    }

    @Test
    public void test医科告示ユニークid() throws IOException {
        String inTxtFile = "in/04/i/txt/ke.txt";
        String outTxtFile = "debug/out/04/i/k-tree-uniq.txt";
        Node root = new 医科告示読み込み().parse(inTxtFile);
        makeUniqId(root);
        makeUniqPath(root);
        root.summary(outTxtFile);
    }
}
