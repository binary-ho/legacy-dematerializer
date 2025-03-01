package binary.ho.model;

public class Query {

    private final SqlType type;
    private final String body;

    public Query(SqlType type, String body) {
        this.type = type;
        this.body = body;
    }
}
