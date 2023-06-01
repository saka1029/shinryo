package saka1029.shinryo.renderer;

import java.io.IOException;

import saka1029.shinryo.common.TextWriter;
import saka1029.shinryo.parser.IKParser;
import saka1029.shinryo.parser.Node;
import saka1029.shinryo.parser.Token;

public class IRenderer extends Renderer {
        public IRenderer(String outDir) throws IOException {
            super(outDir);
        }

        void link(Node node, int level, TextWriter writer) throws IOException {
            Token t = node.token;
            String fileName = node.path + ".html";
            writeLink(node, level, fileName, writer);
            render(node, t.number + " " + t.header, fileName);
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
            if (node.token.type == IKParser.通則 || childContainsAny(node, IKParser.区分, IKParser.数字))
                link(node, level, w);
            else if (node.token.type == IKParser.区分番号 && !node.token.header.equals("削除"))
                linkKubun(node, level, w);
            else
                line(node, level, w);
        }

}
