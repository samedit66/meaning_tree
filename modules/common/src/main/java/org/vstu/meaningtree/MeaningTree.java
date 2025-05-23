package org.vstu.meaningtree;

import org.jetbrains.annotations.NotNull;
import org.vstu.meaningtree.nodes.Node;
import org.vstu.meaningtree.utils.NodeIterator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class MeaningTree implements Serializable, Cloneable, Iterable<Node.Info> {
    private Node _rootNode;

    public MeaningTree(Node rootNode) {
        _rootNode = rootNode;
    }

    public Node getRootNode() {
        return _rootNode;
    }

    public void changeRoot(Node node) {_rootNode = node;}

    @Override
    @NotNull
    /**
     * Итератор может выдавать нулевые ссылки
     */
    public Iterator<Node.Info> iterator() {
        return new NodeIterator(_rootNode, true);
    }

    public List<Node.Info> walk() {
        ArrayList<Node.Info> nodes = new ArrayList<>(_rootNode.walkChildren());
        nodes.addFirst(new Node.Info(_rootNode, null, -1, "root"));
        return nodes;
    }

    public Node findParentOfNode(Node node) {
        for (Node.Info inf : this) {
            if (inf.node().equals(node)) {
                return inf.parent();
            }
        }
        return null;
    }

    public String generateDot() {
        return normalizeDot("graph MeaningTree {\ndpi=255;\n" + _rootNode.generateDot() + "}");
    }

    private static String normalizeDot(String dot) {
        String[] lines = dot.split("\n");

        StringBuilder connections = new StringBuilder();
        StringBuilder result = new StringBuilder();

        for (String line : lines) {
            if (line.contains("--") || line.equals("}")) {
                connections.append(line).append("\n");
            }
            else {
                result.append(line).append("\n");
            }
        }

        result.append(connections);

        return result.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        MeaningTree that = (MeaningTree) o;
        return Objects.equals(_rootNode, that._rootNode);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(_rootNode);
    }

    @Override
    public MeaningTree clone() {
        return new MeaningTree(_rootNode.clone());
    }
}

