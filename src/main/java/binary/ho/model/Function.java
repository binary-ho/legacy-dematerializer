package binary.ho.model;

import binary.ho.util.FileNameExtractor;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class Function {

    private final String name;
    private final String body;
    private final Set<String> internalCalls;
    private final Set<String> externalCalls;
    private final List<Query> queries;
    private final boolean isRepresentative;

    public Function(String name, String body, String fileName) {
        this.name = name;
        this.body = body;
        this.internalCalls = new HashSet<>();
        this.externalCalls = new HashSet<>();
        this.queries = new LinkedList<>();
        this.isRepresentative = isRepresentativeFunction(fileName, name);
    }

    private boolean isRepresentativeFunction(String filePath, String functionName) {
        String fileNameLastPart = FileNameExtractor.extractLastPart(filePath);
        return fileNameLastPart.equals(functionName);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Function)) {
            return false;
        }
        Function function = (Function) other;
        return Objects.equals(name, function.name)
            && Objects.equals(body, function.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    public String getName() {
        return name;
    }

    public boolean isRepresentative() {
        return isRepresentative;
    }

    public String getBody() {
        return body;
    }

    public void addSqlTypes(List<Query> queries) {
        this.queries.addAll(queries);
    }
}
