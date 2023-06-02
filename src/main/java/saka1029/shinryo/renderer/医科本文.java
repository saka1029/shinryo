package saka1029.shinryo.renderer;

import java.io.IOException;

import saka1029.shinryo.common.TextWriter;
import saka1029.shinryo.parser.医科告示読み込み;
import saka1029.shinryo.parser.Node;

public class 医科本文 extends 本文 {
	public 医科本文(String outDir) throws IOException {
		super(outDir);
	}

	@Override
	void render(Node node, int level, TextWriter w) throws IOException {
		if (node.token.type == 医科告示読み込み.区分番号 && !node.token.header.equals("削除"))
			writeLinkKubun(node, level, w);
		else if (node.token.type == 医科告示読み込み.通則 && node.children.size() > 0)
			writeLink(node, level, w);
		else if (childContainsAny(node, 医科告示読み込み.区分))
			writeLink(node, level, w);
		else if (isAny(node, 医科告示読み込み.章, 医科告示読み込み.部, 医科告示読み込み.節, 医科告示読み込み.款)
				&& childContainsAny(node, 医科告示読み込み.区分, 医科告示読み込み.区分番号, 医科告示読み込み.数字))
			writeLink(node, level, w);
		else
			writeLine(node, level, w);
	}

}
