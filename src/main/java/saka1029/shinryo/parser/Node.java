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
     * ルート(parent == null)のときは常にnullです。それ以外の場合はnot nullです。
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
    
    /**
     * 通知ノードの場合にtrueとなります。
     */
    public final boolean isTuti;

    private Node(boolean isTuti, Node parent, Token token, String id, String path, int level) {
        this.isTuti = isTuti;
        this.parent = parent;
        this.token = token;
        this.id = id;
        this.path = path;
        this.level = level;
    }

    public static Node root(boolean isTuti) {
        return new Node(isTuti, null, null, null, null, 0);
    }

    public boolean isRoot() {
        return parent == null;
    }

    public Node addChild(Token token) {
        Objects.requireNonNull(token, "token");
        // id, pathはパース後にユニーク化するために更新する点に注意する。
        String childId = token.id;
        String childPath = isRoot() ? childId : path + Pat.パス区切り + childId;
        // isTutiは親の値を引き継ぎます。
        Node child = new Node(isTuti, this, token, childId, childPath, level + 1);
        children.add(child);
        return child;
    }
    
    /**
     * コピーを返します。
     * コピーは元のオブジェクトとbodyを共有します。
     * childrenはシャローコピーです。
     */
    public Node copy() {
        // isTutiは元のNodeの値を引き継ぎます。
        Node copy = new Node(isTuti, parent, token, id, path, level);
        copy.children.addAll(children);
        return copy;
    }

    /**
     * Nodeを深さ優先でたどりながら以下の情報を出力します。
     * path, number, header, PDFファイル名, ページ番号, テキストファイル行番号, indent, bodyの行数, 通知あり
     */
    public void summary(Consumer<String> consumer) {
        Node node = this;
        if (!node.isRoot()) {
            // マージで追加されたNodeの場合、通知Nodeに置き換える。
            if (node.token == null)
                node = tuti;
            Token t = node.token;
            consumer.accept("%s%s%s %s : %s:%d %s:%d indent=%d body.size=%d%s%s".formatted(path, "  ".repeat(node.level),
                t.number, t.header,
                t.pdfFileName, t.pageNo, t.txtFileName, t.lineNo, t.indent, t.body.size(), node.isTuti ? ":通知" : "", node.tuti == null ? "": ":通知参照"));
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
    
    public void visit(Consumer<Node> visitor) {
    	visitor.accept(this);
    	for (Node child : children)
    		child.visit(visitor);
    }

    @Override
    public String toString() {
        return "Node[path=%s, token=%s]".formatted(path, token);
    }
}
