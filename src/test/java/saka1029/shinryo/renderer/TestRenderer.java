package saka1029.shinryo.renderer;

import java.io.IOException;

import org.junit.Test;

import saka1029.shinryo.parser.Node;
import saka1029.shinryo.parser.TKParser;

public class TestRenderer {

    @Test
    public void testKRenderer() throws IOException {
        String inTxtFile = "data/04tk.txt";
        String outDir = "data/04/t";
        String title = "令和04年調剤点数表";
        String outHtmlFile = "index.html";
        Node root = new TKParser().parse(inTxtFile);
        new KRenderer(outDir).render(root, title, outHtmlFile);
    }

}
