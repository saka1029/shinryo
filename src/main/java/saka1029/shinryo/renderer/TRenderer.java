package saka1029.shinryo.renderer;

import java.io.IOException;

import saka1029.shinryo.common.TextWriter;
import saka1029.shinryo.parser.Node;
import saka1029.shinryo.parser.TKParser;
import saka1029.shinryo.parser.Token;

public class TRenderer extends Renderer {
        public TRenderer(String outDir) throws IOException {
            super(outDir);
        }

        void linkKubun(Node node, int level, TextWriter writer) throws IOException {
            Token t = node.token;
            String fileName = node.id + ".html";
            writeLink(node, level, fileName, writer);
            render(node, t.number + " " + t.header, fileName);
        }

        void line(Node node, int level, TextWriter writer) throws IOException {
            writeLine(node, level, writer);
            for (Node child : node.children)
                render(child, level + 1, writer);
        }

        @Override
        void render(Node node, int level, TextWriter w) throws IOException {
            if (node.token.type == TKParser.区分番号 && node.children.size() > 0)
                linkKubun(node, level, w);
            else
                line(node, level, w);
        }

}
