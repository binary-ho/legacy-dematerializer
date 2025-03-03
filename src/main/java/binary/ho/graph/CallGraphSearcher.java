package binary.ho.graph;

import binary.ho.function.Function;
import binary.ho.function.callee.Callee;
import binary.ho.function.callee.CalleeType;
import binary.ho.module.CModule;
import binary.ho.module.CModules;
import java.util.LinkedList;
import java.util.List;

public class CallGraphSearcher {

    private final CModules CModules;
    private final VisitingNodes visitingNodes;

    private static final String QUERY_PREFIX = "(QUERY) ";

    private CallGraphSearcher(CModules CModules) {
        this.CModules = CModules;
        this.visitingNodes = new VisitingNodes();
    }

    public static Node searchFromModule(String moduleName, CModules CModules) {
        CallGraphSearcher callGraphSearcher = new CallGraphSearcher(CModules);
        return callGraphSearcher.search(moduleName);
    }

    private Node search(String moduleName) {
        CModule cModule = CModules.get(moduleName);
        Function representativeFunction = cModule.getRepresentativeFunction();
        return search(cModule, representativeFunction.getName());
    }

    private Node search(CModule module, String current) {
        if (visitingNodes.isVisiting(current)) {
            return RecursiveNode.create(current);
        }

        if (module.isExternalCall(current)) {
            return search(current);
        }

        Function function = module.getFunction(current);
        if (function.hasNoCallee()) {
            return Node.createdLeafNode(function.getName());
        }

        visitingNodes.visit(current);
        List<Node> nextNodes = getNextNodes(module, function);
        visitingNodes.exit(current);
        return Node.createNode(current, nextNodes);
    }

    private List<Node> getNextNodes(CModule module, Function function) {
        List<Node> nextNodes = new LinkedList<>();
        for (Callee callee : function.getCallees()) {
            if (CalleeType.SQL == callee.getType()) {
                nextNodes.add(createSqlNode(callee));
                continue;
            }

            Node next = search(module, callee.getName());
            nextNodes.add(next);
        }
        return nextNodes;
    }

    private Node createSqlNode(Callee callee) {
        return Node.createdLeafNode(QUERY_PREFIX + callee.getName());
    }
}
