package binary.ho.model;

import binary.ho.parser.FileNameExtractor;

import java.util.*;

public class Function {
    private final String name;
    private final String body;
    private final Set<String> internalCalls;
    private final Set<String> externalCalls;
    private final List<String> sqlTypes;
    private final boolean isRepresentative;

    public Function(String name, String body, String fileName) {
        this.name = name;
        this.body = body;
        this.internalCalls = new HashSet<>();
        this.externalCalls = new HashSet<>();
        this.sqlTypes = new LinkedList<>();
        this.isRepresentative = isRepresentativeFunction(fileName, name);
    }

    private boolean isRepresentativeFunction(String filePath, String functionName) {
        String fileNameLastPart = FileNameExtractor.extractLastPart(filePath);
        return fileNameLastPart.equals(functionName);
    }
}
