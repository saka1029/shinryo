package saka1029.shinryo.renderer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.junit.Test;

import saka1029.shinryo.common.Common;
import saka1029.shinryo.common.Param;
import saka1029.shinryo.parser.Node;
import saka1029.shinryo.parser.Parser;
import saka1029.shinryo.parser.Token;
import saka1029.shinryo.parser.施設基準告示読込;

public class Test施設基準マッチング {

	static final Logger LOGGER = Common.logger(Test施設基準マッチング.class);
	
	static Param param = Param.of("in", "debug/out", "02");
	
	@Test
	public void test() throws IOException {
		String 点数表 = "k";
		Node kRoot = Parser.parse(new 施設基準告示読込(), false, param.txt(点数表, "ke"));
		Map<String, List<Node>> map = new HashMap<>();
		kRoot.visit(n -> {
			if (n.token == null)
				return;
			Token token = n.token;
			if (!token.header.endsWith("の基準") && !token.header.endsWith("の施設基準"))
				return;
			String ns = token.header.replaceAll("\\s", "").replaceFirst("(の基準|の施設基準)$", "");
			String[] names = ns.split("、|及び");
			for (String name : names)
				map.computeIfAbsent(name, k -> new ArrayList<>()).add(n);
		});
		for (Entry<String, List<Node>> e : map.entrySet()) {
			System.out.printf("%s:%n", e.getKey());
			for (Node n : e.getValue())
				System.out.printf("  %s %s %s%n", n.path, n.token.number, n.token.header);
		}

//		Node tRoot = Parser.parse(new 施設基準通知読込(), false, param.txt(点数表, "te"));
//		List<Node> tNodes = new ArrayList<>();
//		tRoot.visit(n -> {
//			
//		});
	}

}
