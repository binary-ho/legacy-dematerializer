package binary.ho.app;

import binary.ho.graph.FunctionCallGraphSearcher;
import binary.ho.module.CModules;

public class FunctionCallGraphApplication {

    private static final String CACHE_DIRECTORY = "cache/modules.ser";
    private static final String CALL_GRAPH_FILE_PREFIX = "function_call_";

    public static void main(String[] args) {
        System.out.println("=== Function Call Graph 생성 ===\n");

        CModules cModules = CModuleLoader.loadFromCacheFile(CACHE_DIRECTORY);
        CallGraphGenerator callGraphGenerator = new CallGraphGenerator(CALL_GRAPH_FILE_PREFIX);
        callGraphGenerator.generateCallGraph(new FunctionCallGraphSearcher(cModules));
    }
}
