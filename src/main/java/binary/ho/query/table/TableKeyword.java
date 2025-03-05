package binary.ho.query.table;

import binary.ho.query.SqlType;

public enum TableKeyword {
    SELECT(SqlType.SELECT, "FROM"),
    INSERT(SqlType.INSERT, "INTO"),
    MERGE(SqlType.MERGE, "INTO"),
    UPDATE(SqlType.UPDATE, "UPDATE"),
    DELETE(SqlType.DELETE, "FROM"),
    NOT_SUPPORTED(SqlType.QUERY, "MISSING"),
    ;

    private final SqlType sqlType;
    private final String from;

    TableKeyword(SqlType sqlType, String from) {
        this.sqlType = sqlType;
        this.from = from;
    }

    public String getKeyword() {
        return from;
    }

    public static TableKeyword of(SqlType sqlType) {
        for (TableKeyword tableKeyword : values()) {
            if (tableKeyword.sqlType == sqlType) {
                return tableKeyword;
            }
        }
        return NOT_SUPPORTED;
    }
}
