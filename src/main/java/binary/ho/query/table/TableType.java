package binary.ho.query.table;

import java.io.Serializable;

public enum TableType implements Serializable {
    MAIN_TABLE("One"),
    VIEW_TABLE("Multi"),
    TABLE_NOT_FOUND(""),
    NOT_SUPPORTED_TABLE("");

    private final String dtoPrefix;
    private static final long serialVersionUID = 1L;

    TableType(String dtoPrefix) {
        this.dtoPrefix = dtoPrefix;
    }

    public String getDtoPrefix() {
        return dtoPrefix;
    }

    public boolean isNoDto() {
        return this == TABLE_NOT_FOUND || this == NOT_SUPPORTED_TABLE;
    }
}
