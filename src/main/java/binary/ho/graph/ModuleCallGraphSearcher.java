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

public class ModuleCallGraphSearcher {

    private final CModules CModules;
    private final VisitingNodes visitingNodes;

    private ModuleCallGraphSearcher(CModules CModules) {
        this.CModules = CModules;
        this.visitingNodes = new VisitingNodes();
    }

    public static Node searchFromModule(String moduleName, CModules CModules) {
        ModuleCallGraphSearcher callGraphSearcher = new ModuleCallGraphSearcher(CModules);
        return callGraphSearcher.searchModule(moduleName);
    }

    private Node searchModule(String moduleName) {
        CModule cModule = CModules.get(moduleName);
        return searchModule(cModule);
    }

    private Node searchModule(CModule cModule) {
        Function function = cModule.getRepresentativeFunction();
        String functionName = function.getName();

        if (visitingNodes.isVisiting(functionName)) {
            return Node.createRecursiveNode(functionName);
        }

        visitingNodes.visit(functionName);
        List<Node> externalCallNodes = getExternalCallNodes(cModule, function);
        visitingNodes.exit(functionName);

        return Node.createNode(Callee.from(function), externalCallNodes);
    }

    private List<Node> getExternalCallNodes(CModule module, Function function) {
        List<Node> externalCallNodes = new LinkedList<>();
        for (Callee callee : function.getCallees()) {
            if (isSqlCallee(callee)) {
                externalCallNodes.add(createSqlNode((SqlCallee) callee));
                continue;
            }

            if (module.isExternalCall(callee.getName())) {
                Node externalNode = searchModule(callee.getName());
                externalCallNodes.add(externalNode);
                continue;
            }

            Function calleeFunction = module.getFunction(callee.getName());
            externalCallNodes.addAll(getExternalCallNodes(module, calleeFunction));
        }

        return externalCallNodes;
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