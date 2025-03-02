package binary.ho.graph;

public class RecursiveNode {

    private static final String RECURSIVE_FUNCTION_POSTFIX = "(RECURSIVE)";

    public static Node createNode(String functionName) {
        return Node.createdLeafNode(functionName + RECURSIVE_FUNCTION_POSTFIX);
    }
}
