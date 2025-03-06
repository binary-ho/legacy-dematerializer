package binary.ho.graph;

import binary.ho.function.callee.Callee;
import binary.ho.function.callee.CalleeType;
import java.util.List;

public class Node {

    public static final String RECURSIVE_NODE_POSTFIX = " (RECURSIVE)";
    private final Callee callee;
    private final List<Node> nextNodes;

    private Node(Callee callee, List<Node> nextNodes) {
        this.callee = callee;
        this.nextNodes = nextNodes;
    }

    public static Node createNode(Callee callee, List<Node> nextNodes) {
        return new Node(callee, nextNodes);
    }

    public static Node createLeafNode(Callee callee) {
        return new Node(callee, List.of());
    }

    public static Node createSqlNode(String sqlValue) {
        Callee callee = new Callee(sqlValue, CalleeType.SQL);
        return createLeafNode(callee);
    }

    public static Node createRecursiveNode(String functionName) {
        return createLeafNode(
            new Callee(functionName + RECURSIVE_NODE_POSTFIX, CalleeType.FUNCTION));
    }

    public Callee getCallee() {
        return callee;
    }

    public List<Node> getNextNodes() {
        return nextNodes;
    }

    public boolean isLeaf() {
        return nextNodes.isEmpty();
    }
}
