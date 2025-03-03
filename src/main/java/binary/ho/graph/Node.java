package binary.ho.graph;

import java.util.List;

public class Node {

    private final String functionName;
    private final List<Node> nextNodes;

    private Node(String functionName, List<Node> nextNodes) {
        this.functionName = functionName;
        this.nextNodes = nextNodes;
    }

    public static Node createNode(String functionName, List<Node> nextNodes) {
        return new Node(functionName, nextNodes);
    }

    public static Node createLeafNode(String functionName) {
        return new Node(functionName, List.of());
    }

    public String getFunctionName() {
        return functionName;
    }

    public List<Node> getNextNodes() {
        return nextNodes;
    }

    public boolean isLeaf() {
        return nextNodes.isEmpty();
    }
}
