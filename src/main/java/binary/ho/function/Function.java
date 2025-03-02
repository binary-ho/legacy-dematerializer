package binary.ho.function;

import binary.ho.query.Query;
import java.util.List;
import java.util.Objects;

public class Function {

    // TODO: 현재는 같은 이름의 함수가 없지만, 직접 테스트 해봐야 한다.
    private final String name;
    private final List<Query> queries;
    private final List<String> callees;

    public Function(String name, List<Query> queries, List<String> callees) {
        this.name = name;
        this.queries = queries;
        this.callees = callees;
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
        return Objects.equals(name, function.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    public String getName() {
        return name;
    }

    public List<String> getCallees() {
        return List.copyOf(callees);
    }

    public boolean hasNoCallee() {
        return callees.isEmpty();
    }
}
