package binary.ho.query;

import binary.ho.query.table.Table;
import java.io.Serializable;

public class Query implements Serializable {

    private static final long serialVersionUID = 1L;

    private final SqlType type;
    private final Table mainTable;

    public Query(SqlType type, Table mainTable) {
        this.type = type;
        this.mainTable = mainTable;
    }

    public SqlType getType() {
        return type;
    }

    public Table getMainTable() {
        return mainTable;
    }
}