package binary.ho.model;

import binary.ho.parser.FileNameParser;
import java.util.HashMap;
import java.util.Map;

public class Module {

    private final String filePath;
    private final String fileName;
    private final Map<String, Function> functions;

    public Module(String filePath) {
        this.filePath = filePath;
        this.fileName = FileNameParser.getFileName(filePath);
        this.functions = new HashMap<>();
    }

    public void addFunction(Function function) {
        functions.put(function.getName(), function);
    }

    public Function getRepresentativeFunction() {
        return functions.values().stream()
            .filter(Function::isRepresentative)
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("No representative function"));
    }
}
