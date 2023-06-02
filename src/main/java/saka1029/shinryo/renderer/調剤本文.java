package saka1029.shinryo.renderer;

import java.io.IOException;

import saka1029.shinryo.common.TextWriter;
import saka1029.shinryo.parser.Node;
import saka1029.shinryo.parser.調剤告示読み込み;

public class 調剤本文 extends 本文 {
	public 調剤本文(String outDir) throws IOException {
		super(outDir);
	}

	@Override
	void render(Node node, int level, TextWriter w) throws IOException {
		if (node.token.type == 調剤告示読み込み.区分番号 && node.children.size() > 0)
			writeLinkKubun(node, level, w);
		else
			writeLine(node, level, w);
	}

}
