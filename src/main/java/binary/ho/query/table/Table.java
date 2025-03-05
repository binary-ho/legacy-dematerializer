package binary.ho.query.table;

public class Table {

    private final String name;

    private Table(String name) {
        this.name = name;
    }

    public static Table create(String name) {
        return new Table(name);
    }

    public static Table createViewTable() {
        return new Table(TableType.VIEW_TABLE.name());
    }

    public static Table createNotFoundTable() {
        return new Table(TableType.TABLE_NOT_FOUND.name());
    }

    public static Table createNotSupportedTable() {
        return new Table("");
    }

    public String getName() {
        return name;
    }
}
