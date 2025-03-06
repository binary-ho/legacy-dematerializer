package binary.ho.function.callee;

import binary.ho.query.Query;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public class Callee implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String name;
    private final CalleeType type;

    protected Callee(String name, CalleeType type) {
        this.name = name;
        this.type = type;
    }

    public static List<Callee> from(List<String> callees) {
        return callees.stream()
            .map(callee -> new Callee(callee, CalleeType.FUNCTION))
            .collect(Collectors.toList());
    }

    public static List<Callee> fromQueries(List<Query> queries) {
        return queries.stream()
            .map(SqlCallee::new)
            .collect(Collectors.toList());
    }

    public String getName() {
        return name;
    }

    public CalleeType getType() {
        return type;
    }
}
