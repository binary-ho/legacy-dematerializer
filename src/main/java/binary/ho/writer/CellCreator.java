package binary.ho.writer;

import binary.ho.function.callee.Callee;
import binary.ho.function.callee.CalleeType;
import binary.ho.function.callee.SqlCallee;
import binary.ho.query.Query;
import binary.ho.query.QueryCellValueBuilder;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;

public class CellCreator {

    private final CellStyle functionCellStyle;
    private final CellStyle sqlCellStyle;

    public CellCreator(Workbook workbook) {
        this.functionCellStyle = CellStyleCreator.createFrom(workbook, CalleeType.FUNCTION);
        this.sqlCellStyle = CellStyleCreator.createFrom(workbook, CalleeType.SQL);
    }

    public void createFromCallee(Row row, int depth, Callee callee, ChildPosition position) {
        Cell cell = row.createCell(depth);
        String cellValue = getValue(callee);
        cell.setCellValue(drawPosition(position, cellValue));
        cell.setCellStyle(getCellStyle(callee));
    }

    public void createFromValue(Row row, int depth, String value) {
        Cell cell = row.createCell(depth);
        cell.setCellValue(value);
        cell.setCellStyle(functionCellStyle);
    }

    private String getValue(Callee callee) {
        if (isSqlCallee(callee)) {
            SqlCallee sqlCallee = (SqlCallee) callee;
            Query query = sqlCallee.getQuery();
            return QueryCellValueBuilder.build(query);
        }

        return callee.getName();
    }

    private String drawPosition(ChildPosition childPosition, String value) {
        return childPosition.getBranch() + value;
    }

    private CellStyle getCellStyle(Callee callee) {
        if (isSqlCallee(callee)) {
            return sqlCellStyle;
        }
        return functionCellStyle;
    }

    private boolean isSqlCallee(Callee callee) {
        return callee instanceof SqlCallee;
    }
}
