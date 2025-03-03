package binary.ho.query;

import java.io.Serializable;

public class Query implements Serializable {

    private static final long serialVersionUID = 1L;

    private final SqlType type;

    public Query(SqlType type) {
        this.type = type;
    }

    public SqlType getType() {
        return type;
    }
}
