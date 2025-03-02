package binary.ho.graph;

import binary.ho.function.Function;
import binary.ho.module.CModule;
import binary.ho.module.ModuleMapper;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Searcher {

    private final ModuleMapper moduleMapper;
    private final Set<String> visiting;

    private Searcher(ModuleMapper moduleMapper) {
        this.moduleMapper = moduleMapper;
        this.visiting = new HashSet<>();
    }

    public static Node searchFromModule(String moduleName, ModuleMapper moduleMapper) {
        Searcher searcher = new Searcher(moduleMapper);
        return searcher.search(moduleName);
    }

    private Node search(String moduleName) {
        CModule cModule = moduleMapper.get(moduleName);
        Function representativeFunction = cModule.getRepresentativeFunction();
        return search(cModule, representativeFunction.getName());
    }

    private Node search(CModule module, String current) {
        if (visiting.contains(current)) {
            return RecursiveNode.createNode(current);
        }

        if (module.isExternalCall(current)) {
            return search(current);
        }

        Function function = module.getFunction(current);
        if (function.hasNoCallee()) {
            return Node.createdLeafNode(function.getName());
        }

        visiting.add(current);
        List<Node> nextNodes = getNextNodes(module, function);
        visiting.remove(current);
        return Node.createNode(current, nextNodes);
    }

    private List<Node> getNextNodes(CModule module, Function function) {
        List<Node> nextNodes = new LinkedList<>();
        for (String callee : function.getCallees()) {
            Node next = search(module, callee);
            nextNodes.add(next);
        }
        return nextNodes;
    }
}
