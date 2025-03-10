package binary.ho.app;

import binary.ho.module.CModules;

public class ModuleCallGraphApplication {

    private static final String CACHE_DIRECTORY = "cache/modules.ser";
    private static final String CALL_GRAPH_FILE_PREFIX = "module_graph_";

    public static void main(String[] args) {
        System.out.println("=== Module Call Graph 생성 ===\n");

        CModules cModules = CModuleLoader.loadFromCacheFile(CACHE_DIRECTORY);
        EagerCallGraphGenerator generator = new EagerCallGraphGenerator(
            cModules, CALL_GRAPH_FILE_PREFIX);
        generator.generateCallGraph();
    }
}
