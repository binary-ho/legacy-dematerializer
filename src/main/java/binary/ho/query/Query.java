package binary.ho.query;

import binary.ho.query.table.Table;
import java.io.Serializable;

public class Query implements Serializable {

    private static final long serialVersionUID = 1L;

    private final SqlType type;
    private final Table mainTable;
    private final String query;

    public Query(SqlType type, Table mainTable, String query) {
        this.type = type;
        this.mainTable = mainTable;
        this.query = query;
    }

    public SqlType getType() {
        return type;
    }

    public Table getMainTable() {
        return mainTable;
    }

    public String getQuery() {
        return query;
    }
}