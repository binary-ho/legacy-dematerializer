package binary.ho.query;

import binary.ho.query.table.Table;
import binary.ho.query.table.TableType;

public class QueryCellValueBuilder {

    private static final String QUERY_PREFIX = "(QUERY) ";
    private static final String LINE_BREAK = "\n";
    public static final String NO_DTO = "";

    public static String build(Query query) {
        SqlType queryType = query.getType();
        Table mainTable = query.getMainTable();
        return QUERY_PREFIX
            + queryType.name()
            + " "
            + mainTable.getName()
            + LINE_BREAK
            + buildDtoName(queryType, mainTable)
            + LINE_BREAK
            + LINE_BREAK
            + query.getQuery();
    }

    private static String buildDtoName(SqlType queryType, Table mainTable) {
        TableType tableType = mainTable.getType();
        if (tableType.isNoDto()) {
            return NO_DTO;
        }

        String dtoPrefix = tableType.getDtoPrefix();
        return queryType.name().toLowerCase() + dtoPrefix + "(ASIS/" + mainTable.getName() + ")";
    }
}
