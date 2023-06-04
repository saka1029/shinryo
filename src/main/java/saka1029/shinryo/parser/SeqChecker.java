package saka1029.shinryo.parser;

import java.util.function.Consumer;

public class SeqChecker {

	void check(Node root, Consumer<String> writer) {
		int prev = 0;
		for (Node child : root.children) {
			if (child.token.type.name.equals("区分番号"))
				continue;
			
		}
	}
}
