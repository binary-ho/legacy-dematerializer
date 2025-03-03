package binary.ho.module;

import binary.ho.file.FileNameExtractor;
import binary.ho.function.Function;
import binary.ho.function.MissingFunctionFactory;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CModule implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String moduleName;
    private final Map<String, Function> functions;
    private final Function representativeFunction;

    public CModule(String filePath, List<Function> functions) {
        this.moduleName = FileNameExtractor.extractLastPart(filePath);
        this.representativeFunction = findRepresentativeFunction(this.moduleName, functions);
        this.functions = collectToMap(functions);
    }

    private Map<String, Function> collectToMap(List<Function> functions) {
        return functions.stream()
            .collect(Collectors.toMap(Function::getName, function -> function));
    }

    private Function findRepresentativeFunction(String moduleName, List<Function> functions) {
        if (functions.isEmpty()) {
            return MissingFunctionFactory.create();
        }

        return functions.stream()
            .filter(function -> function.getName().equals(moduleName))
            .findFirst()
            .orElseGet(() -> getFirst(functions));
    }

    private Function getFirst(List<Function> functions) {
        return functions.get(0);
    }

    public String getModuleName() {
        return moduleName;
    }

    public Function getRepresentativeFunction() {
        return representativeFunction;
    }

    public Function getFunction(String functionName) {
        return functions.get(functionName);
    }

    public boolean isExternalCall(String functionName) {
        return !functions.containsKey(functionName);
    }
}
