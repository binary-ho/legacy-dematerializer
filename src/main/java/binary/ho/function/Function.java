package binary.ho.function;

import binary.ho.query.Query;
import java.util.List;
import java.util.Objects;

public class Function {

    private final String name;
    private final String body;
    private final List<Query> queries;
    private final List<String> functionCalls;

    public Function(String name, String body, List<Query> queries, List<String> functionCalls) {
        this.name = name;
        this.body = body;
        this.queries = queries;
        this.functionCalls = functionCalls;
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

    public String getBody() {
        return body;
    }
}
