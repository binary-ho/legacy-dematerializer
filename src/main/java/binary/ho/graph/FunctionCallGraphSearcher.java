package binary.ho.graph;

import binary.ho.function.Function;
import binary.ho.function.callee.Callee;
import binary.ho.function.callee.SqlCallee;
import binary.ho.module.CModule;
import binary.ho.module.CModules;
import binary.ho.query.Query;
import binary.ho.query.QueryCellValueBuilder;
import java.util.LinkedList;
import java.util.List;

public class FunctionCallGraphSearcher implements Searcher {

    private final CModules CModules;
    private final VisitingNodes visitingNodes;

    public FunctionCallGraphSearcher(CModules CModules) {
        this.CModules = CModules;
        visitingNodes = new VisitingNodes();
    }

    @Override
    public Node search(String moduleName) {
        CModule cModule = CModules.get(moduleName);
        Function representativeFunction = cModule.getRepresentativeFunction();
        return searchFunction(cModule, representativeFunction.getName());
    }

    private Node searchFunction(CModule module, String current) {
        if (visitingNodes.isVisiting(current)) {
            return Node.createRecursiveNode(current);
        }

        if (module.isExternalCall(current)) {
            return search(current);
        }

        Function function = module.getFunction(current);
        Callee callee = Callee.from(function);
        if (function.hasNoCallee()) {
            return Node.createLeafNode(callee);
        }

        visitingNodes.visit(current);
        List<Node> nextNodes = getNextNodes(module, function);
        visitingNodes.exit(current);
        return Node.createNode(callee, nextNodes);
    }

    private List<Node> getNextNodes(CModule module, Function function) {
        List<Node> nextNodes = new LinkedList<>();
        for (Callee callee : function.getCallees()) {
            if (isSqlCallee(callee)) {
                nextNodes.add(createSqlNode((SqlCallee) callee));
                continue;
            }

            Node next = searchFunction(module, callee.getName());
            nextNodes.add(next);
        }
        return nextNodes;
    }

    private boolean isSqlCallee(Callee callee) {
        return callee instanceof SqlCallee;
    }

    private Node createSqlNode(SqlCallee callee) {
        Query query = callee.getQuery();
        String cellValue = QueryCellValueBuilder.build(query);
        return Node.createSqlNode(cellValue);
    }
}
