package binary.ho.graph;

public class RecursiveNodeFactory {

    private static final String RECURSIVE_FUNCTION_POSTFIX = "(RECURSIVE) ";

    private RecursiveNodeFactory() {
    }

    public static Node create(String functionName) {
        return Node.createLeafNode(functionName + RECURSIVE_FUNCTION_POSTFIX);
    }
}
