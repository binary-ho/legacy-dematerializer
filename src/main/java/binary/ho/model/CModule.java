package binary.ho.model;

import binary.ho.util.FileNameExtractor;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CModule {

    private final String filePath;
    private final Map<String, Function> functions;
    private final Function representativeFunction;

    public CModule(String filePath, List<Function> functions) {
        this.filePath = filePath;
        this.representativeFunction = findRepresentativeFunction(functions);
        this.functions = collectToMap(functions);
    }

    private Map<String, Function> collectToMap(List<Function> functions) {
        return functions.stream()
            .collect(Collectors.toMap(Function::getName, function -> function));
    }

    private Function findRepresentativeFunction(List<Function> functions) {
        String fileNameLastPart = FileNameExtractor.extractLastPart(filePath);
        return functions.stream()
            .filter(function -> function.getName().equals(fileNameLastPart))
            .findFirst()
            .orElseGet(() -> getFirst(functions));
    }

    private Function getFirst(List<Function> functions) {
        if (functions.isEmpty()) {
            throw new IllegalArgumentException("functions list is empty");
        }
        return functions.get(0);
    }
}
