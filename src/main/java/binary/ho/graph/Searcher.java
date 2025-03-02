package binary.ho.graph;

import binary.ho.function.Function;
import binary.ho.module.CModule;
import binary.ho.module.ModuleMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Searcher {

    private final ModuleMapper moduleMapper;

    public Searcher(ModuleMapper moduleMapper) {
        this.moduleMapper = moduleMapper;
    }

    public Node search(String moduleName) {
        CModule cModule = moduleMapper.get(moduleName);
        Function representativeFunction = cModule.getRepresentativeFunction();
        return search(cModule, representativeFunction.getName());
    }

    private Node search(CModule module, String current) {
        if (module.isExternalCall(current)) {
            return search(current);
        }

        Map<String, Function> functions = module.getFunctions();
        Function function = functions.get(current);
        if (function.hasNoCallee()) {
            return Node.createdLeafNode(function.getName());
        }

        List<Node> nextNodes = getNextNodes(module, function);
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
