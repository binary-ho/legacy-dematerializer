package binary.ho.query.table;

public class Table {

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

    public String getName() {
        return name;
    }
}
