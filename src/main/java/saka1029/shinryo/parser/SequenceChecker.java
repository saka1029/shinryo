package saka1029.shinryo.parser;

import java.util.List;
import java.util.function.Consumer;

public class SequenceChecker {

    static final List<String> NO_CHECK_TYPES = List.of("区分番号", "注");

    void check(Node root, Consumer<String> writer) {
        Node prevNode = null;
        int prevId = 0;
        for (Node child : root.children) {
            if (!NO_CHECK_TYPES.contains(child.token.type.name)) {
                String d = child.id.replaceAll("\\D", "");
                if (!d.isEmpty()) {
                    int id = Integer.parseInt(d);
                    if (id != prevId + 1)
                        writer.accept("順序誤り: " + child.token.toString());
                    prevNode = child;
                    prevId = id;
                }
            }
            check(child, writer);
        }
    }
}
