package binary.ho.writer;

import binary.ho.function.callee.Callee;
import binary.ho.function.callee.CalleeType;
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

        String cellValue = getCellValue(callee, position);
        cell.setCellValue(cellValue);
        cell.setCellStyle(getCellStyle(callee.getType()));
    }

    public void createFromValue(Row row, int depth, String value) {
        Cell cell = row.createCell(depth);
        cell.setCellValue(value);
        cell.setCellStyle(getCellStyle(CalleeType.FUNCTION));
    }

    private String getCellValue(Callee callee, ChildPosition childPosition) {
        return childPosition.getBranch() + callee.getName();
    }

    private CellStyle getCellStyle(CalleeType calleeType) {
        if (CalleeType.SQL == calleeType) {
            return sqlCellStyle;
        }
        return functionCellStyle;
    }
}
