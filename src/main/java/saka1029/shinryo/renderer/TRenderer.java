package saka1029.shinryo.renderer;

import java.io.IOException;

import saka1029.shinryo.common.TextWriter;
import saka1029.shinryo.parser.Node;
import saka1029.shinryo.parser.TKParser;

public class TRenderer extends Renderer {
        public TRenderer(String outDir) throws IOException {
            super(outDir);
        }

        @Override
        void render(Node node, int level, TextWriter w) throws IOException {
            if (node.token.type == TKParser.区分番号 && node.children.size() > 0)
                writeLinkKubun(node, level, w);
            else
                writeLine(node, level, w);
        }

}
