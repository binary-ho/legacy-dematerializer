package binary.ho.graph;

import java.util.HashSet;
import java.util.Set;

public class VisitingNodes {

    private final Set<String> visiting;
    private static final Set<String> EXCLUDED_NODES = Set.of("MISSING");

    public VisitingNodes() {
        this.visiting = new HashSet<>();
    }

    public boolean isVisiting(String functionName) {
        return visiting.contains(functionName);
    }

    public void visit(String functionName) {
        if (EXCLUDED_NODES.contains(functionName)) {
            return;
        }
        
        visiting.add(functionName);
    }

    public void exit(String functionName) {
        visiting.remove(functionName);
    }
}
