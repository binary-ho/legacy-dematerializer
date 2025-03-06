package binary.ho.query.table;

import java.io.Serializable;

public class Table implements Serializable {

    private static final long serialVersionUID = 1L;
    private final String name;
    private final TableType type;

    private Table(String name, TableType type) {
        this.name = name;
        this.type = type;
    }

    public static Table create(String name) {
        return new Table(name, TableType.MAIN_TABLE);
    }

    public static Table createViewTable() {
        return new Table(TableType.VIEW_TABLE.name(), TableType.VIEW_TABLE);
    }

    public static Table createNotFoundTable() {
        return new Table(TableType.TABLE_NOT_FOUND.name(), TableType.TABLE_NOT_FOUND);
    }

    public static Table createNotSupportedTable() {
        return new Table("", TableType.NOT_SUPPORTED_TABLE);
    }

    public String getName() {
        return name;
    }

    public TableType getType() {
        return type;
    }
}
