package saka1029.shinryo.renderer;

import java.io.IOException;

import saka1029.shinryo.common.TextWriter;
import saka1029.shinryo.parser.IKParser;
import saka1029.shinryo.parser.Node;

public class IRenderer extends Renderer {
	public IRenderer(String outDir) throws IOException {
		super(outDir);
	}

	@Override
	void render(Node node, int level, TextWriter w) throws IOException {
		if (node.token.type == IKParser.区分番号 && !node.token.header.equals("削除"))
			writeLinkKubun(node, level, w);
		else if (node.token.type == IKParser.通則 && node.children.size() > 0)
			writeLink(node, level, w);
		else if (childContainsAny(node, IKParser.区分))
			writeLink(node, level, w);
		else if (isAny(node, IKParser.章, IKParser.部, IKParser.節, IKParser.款)
				&& childContainsAny(node, IKParser.区分, IKParser.区分番号, IKParser.数字))
			writeLink(node, level, w);
		else
			writeLine(node, level, w);
	}

}
