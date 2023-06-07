package saka1029.shinryo.parser;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class Node {
    /**
     * ルートの場合nullです。それ以外の場合はnot nullです。
     */
    public final Node parent;
    /**
     * ルート(parent == null)のときは常にnullです。 マージで追加されたNodeの場合もnullです。 それ以外の場合はnot nullです。
     */
    public final Token token;
    /**
     * idはパース後にユニークにするための更新を行うためfinalではありません。
     */
    public String id;
    /**
     * pathはパース後にユニークにするための更新を行うためfinalではありません。
     */
    public String path;
    public final int level;
    /**
     * パース後は常にnullです。マージ後にnot nullになることがあります。
     */
    public Node tuti;
    /**
     * 常にnot nullです。
     */
    public final List<Node> children = new ArrayList<>();

    private Node(Node parent, Token token, String id, String path, int level) {
        this.parent = parent;
        this.token = token;
        this.id = id;
        this.path = path;
        this.level = level;
    }

    public static Node root() {
        return new Node(null, null, null, null, 0);
    }

    public boolean isRoot() {
        return parent == null;
    }

    public Node addChild(Token token) {
        Objects.requireNonNull(token, "token");
        // id, pathはパース後にユニーク化するために更新する点に注意する。
        String childId = token.id;
        String childPath = isRoot() ? childId : path + Pat.パス区切り + childId;
        Node child = new Node(this, token, childId, childPath, level + 1);
        children.add(child);
        return child;
    }
    
    /**
     * マージで通知Nodeに対応する告示Nodeが存在しないときに対応する
     * 告示Nodeを追加するために使用します。
     * 追加された告示Nodeのtokenはnullとなる点に注意してください。
     * 追加位置が不明なため、children.add(child)を実行していない点に注意してください。
     * childrenへの追加は呼び出し元で行う必要があります。
     */
    public Node addChild(String id, String path, Node tuti) {
        Node child = new Node(this, null, id, path, level + 1);
        child.tuti = tuti;
        return child;
    }

    /**
     * Nodeを深さ優先でたどりながら以下の情報を出力します。
     * path, number, header, PDFファイル名, ページ番号, テキストファイル行番号, indent, 通知あり
     */
    public void summary(Consumer<String> consumer) {
        Node node = this;
        if (!node.isRoot()) {
            // マージで追加されたNodeの場合、通知Nodeに置き換える。
            if (node.token == null)
                node = tuti;
            Token t = node.token;
            consumer.accept("%s%s%s %s : %s:%d:%d:%d:%d%s".formatted(path, "  ".repeat(node.level),
                t.number, t.header,
                t.fileName, t.pageNo, t.lineNo, t.indent, t.body.size(), node.tuti == null ? "" : ":通知"));
        }
        for (Node child : node.children)
            child.summary(consumer);
    }

    public void summary(String outTxtFile) throws IOException {
        Files.createDirectories(Path.of(outTxtFile).getParent());
        try (PrintWriter w = new PrintWriter(outTxtFile)) {
            summary(w::println);
        }
    }

    @Override
    public String toString() {
        return "Node[path=%s, token=%s]".formatted(path, token);
    }
}
