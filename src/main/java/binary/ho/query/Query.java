package binary.ho.query;

public class Query {

    private final SqlType type;

    public Query(SqlType type) {
        this.type = type;
    }

    public SqlType getType() {
        return type;
    }
}
