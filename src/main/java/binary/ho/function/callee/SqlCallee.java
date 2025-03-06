package binary.ho.function.callee;

import binary.ho.query.Query;
import java.io.Serializable;

public class SqlCallee extends Callee implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Query query;

    public SqlCallee(Query query) {
        super(query.toString(), CalleeType.SQL);
        this.query = query;
    }

    public Query getQuery() {
        return query;
    }
}
