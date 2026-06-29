package saka1029.shinryo.renderer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.logging.Logger;

import org.junit.Test;

import saka1029.shinryo.common.Common;
import saka1029.shinryo.common.Param;
import saka1029.shinryo.parser.Node;
import saka1029.shinryo.parser.疑義解釈読込;

public class Test疑義解釈本文 {

    static final Logger LOGGER = Common.logger(Test疑義解釈本文.class);

    record NameNode(String name, Node node) {
        @Override
        public final boolean equals(Object arg0) {
            return arg0 instanceof NameNode right
                && Objects.equals(name, right.name);
        }
    }

    /**
     * 疑義のツリーを
     * 分類および名称でソートします。
     */
    public static Map<NameNode, Map<NameNode, List<Node>>> 分類(Node root) {
        Map<NameNode, Map<NameNode, List<Node>>> result = new LinkedHashMap<>();
        Consumer<Node> visitor = new Consumer<>() {
            Map<NameNode, List<Node>> 分類 = null;
            List<Node> 名称 = null;
            @Override
            public void accept(Node node) {
                if (node.token == null) return;
                NameNode nn = new NameNode(node.token.number, node);
                switch (node.token.type.name) {
                    case "分類":
                        分類 = result.computeIfAbsent(nn, k -> new LinkedHashMap<>());
                        break;
                    case "名称":
                        名称 = 分類.computeIfAbsent(nn, k -> new ArrayList<>());
                        break;
                    case "問":
                        名称.add(node);
                        break;
                }
            }
        };
        root.visit(visitor);
        return result;
    }

    @Test
    public void test再編成() throws IOException {
        LOGGER.info(Common.methodName());
        Param param = Param.of("in", "debug/out", "08");
        String inTxtFile = param.inFile("g", "txt/ke.txt");
        Node root = new 疑義解釈読込().parse(inTxtFile);
        Map<NameNode, Map<NameNode, List<Node>>> result = 分類(root);
        int 名称max = 0;
        for (var bunrui : result.entrySet()) {
            System.out.println(bunrui.getKey().name);
            for (var meisyo : bunrui.getValue().entrySet()) {
                名称max = Math.max(名称max, meisyo.getValue().size());
                System.out.println("  " + meisyo.getKey().name);
                for (var node : meisyo.getValue())
                    System.out.println("      " + node.token.number + " " + node.token.header);
            }
        }
        System.out.println("分類数:" + result.size());
        System.out.println("最大名称数:" + 名称max);
    }

    @Test
    public void testRender() throws IOException {
        LOGGER.info(Common.methodName());
        Param param = Param.of("in", "debug/out", "08");
        String inTxtFile = param.inFile("g", "txt/ke.txt");
        String title = param.title("g");
        Node root = new 疑義解釈読込().parse(inTxtFile);
        new 疑義解釈本文(param.outDir, root).render(title, "index.html", false);
    }

}
