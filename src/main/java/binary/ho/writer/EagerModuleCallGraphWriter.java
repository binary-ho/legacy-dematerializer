package binary.ho.writer;

import binary.ho.function.Function;
import binary.ho.function.callee.Callee;
import binary.ho.function.callee.CalleeType;
import binary.ho.graph.VisitingNodes;
import binary.ho.module.CModule;
import binary.ho.module.CModules;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

public class EagerModuleCallGraphWriter implements AutoCloseable {

    private final Workbook workbook;
    private final Sheet sheet;
    private final DepthRowIndex depthRowIndex;
    private final CellCreator cellCreator;
    private final CModules cModules;
    private final String outputDirectory;
    private final VisitingNodes visitingNodes;

    private static final int START_DEPTH = 0;
    private static final int START_ROW_INDEX = 1;
    private static final int COLUMN_WIDTH = 50;
    private static final int ROW_HEIGHT = 350;
    private static final String TREE_VERTICAL = " │";
    private static final String SHEET_NAME = "Module Call Graph";
    private static final String RECURSIVE_NODE_POSTFIX = " (RECURSIVE)";

    public EagerModuleCallGraphWriter(CModules cModules, String outputDirectory) {
        this.workbook = getWorkbook();
        this.sheet = createSheet(workbook);
        this.depthRowIndex = new DepthRowIndex();
        this.cellCreator = new CellCreator(workbook);
        this.cModules = cModules;
        this.outputDirectory = outputDirectory;
        this.visitingNodes = new VisitingNodes();
    }

    public void write(String moduleName) {
        depthRowIndex.setNextRow(START_DEPTH, START_ROW_INDEX);
        writeModule(moduleName, START_DEPTH, START_ROW_INDEX, ChildPosition.NO_PARENT);
        writeToFile();
    }

    private int writeModule(String moduleName, int depth, int rowIndex, ChildPosition position) {
        CModule cModule = cModules.get(moduleName);
        Function representativeFunction = cModule.getRepresentativeFunction();
        Callee representativeCallee = Callee.from(representativeFunction);

        String functionName = representativeFunction.getName();
        if (visitingNodes.isVisiting(functionName)) {
            createRecursiveCell(functionName, depth, rowIndex, position);
            return rowIndex + 1;
        }

        visitingNodes.visit(functionName);
        createCell(depth, rowIndex, position, representativeCallee);

        if (representativeFunction.hasNoCallee()) {
            depthRowIndex.setNextRow(depth, rowIndex + 1);
            return rowIndex + 1;
        }

        int newNextRow = searchFunction(cModule, representativeFunction, depth + 1, rowIndex);
        visitingNodes.exit(functionName);

        depthRowIndex.setNextRow(depth + 1, newNextRow);
        return depthRowIndex.getMaxRowBelowDepth(depth) + 1;
    }

    private int searchFunction(CModule cModule, Function function, int depth, int rowIndex) {
        List<Callee> callees = function.getCallees();
        for (int index = 0; index < callees.size(); index++) {
            Callee callee = callees.get(index);
            String calleeName = callee.getName();

            if (visitingNodes.isVisiting(calleeName)) {
                createRecursiveCell(
                    calleeName, depth, rowIndex, ChildPosition.getPosition(index, callees));
                continue;
            }
            visitingNodes.visit(calleeName);

            ChildPosition childPosition = ChildPosition.getPosition(index, callees);
            int nextRow = depthRowIndex.getNextRow(depth + 1);
            int newNextRow = searchCallee(cModule, callee, depth + 1, nextRow, childPosition);
            depthRowIndex.setNextRow(depth + 1, newNextRow);

            visitingNodes.exit(calleeName);
            if (isLast(index, callees)) {
                connectNodes(depth + 1, rowIndex, nextRow);
            }
        }

        depthRowIndex.setNextRow(depth, rowIndex + 1);
        return depthRowIndex.getMaxRowBelowDepth(depth) + 1;
    }

    private int searchCallee(
        CModule cModule, Callee callee, int depth, int rowIndex, ChildPosition position) {
        if (cModule.isExternalCall(callee.getName())) {
            int nextDepthRow = writeModule(callee.getName(), depth, rowIndex, position);
            depthRowIndex.setNextRow(depth, nextDepthRow);
            return depthRowIndex.getMaxRowBelowDepth(depth) + 1;
        }

        if (callee.isSqlCallee()) {
            createCell(depth, rowIndex, position, callee);
            return depthRowIndex.getMaxRowBelowDepth(depth) + 1;
        }

        Function function = cModule.getFunction(callee.getName());
        if (visitingNodes.isVisiting(function.getName())) {
            createRecursiveCell(function.getName(), depth, rowIndex, position);
            return depthRowIndex.getMaxRowBelowDepth(depth) + 1;
        }

        visitingNodes.visit(function.getName());
        searchFunction(cModule, function, depth, rowIndex);
        visitingNodes.visit(function.getName());
        return depthRowIndex.getMaxRowBelowDepth(depth) + 1;
    }

    private void createCell(int depth, int rowIndex, ChildPosition position,
        Callee representativeCallee) {
        Row row = getRow(rowIndex);
        row.setHeight((short) ROW_HEIGHT);
        cellCreator.createFromCallee(row, depth, representativeCallee, position);
    }

    private boolean isLast(int index, Collection<?> list) {
        return index == list.size() - 1;
    }

    private void connectNodes(int depth, int startIndex, int endIndex) {
        for (int currentRow = startIndex; currentRow <= endIndex; currentRow++) {
            Row row = getRow(currentRow);
            Cell cell = row.getCell(depth);
            if (cell == null) {
                cellCreator.createFromValue(row, depth, TREE_VERTICAL);
            }
        }
    }

    private Row getRow(int currentRow) {
        Row row = sheet.getRow(currentRow);
        if (row != null) {
            return row;
        }
        Row newRow = sheet.createRow(currentRow);
        newRow.setHeight((short) ROW_HEIGHT); // 새로 생성된 행의 높이 고정
        return newRow;
    }

    private void writeToFile() {
        try (FileOutputStream fileOut = new FileOutputStream(outputDirectory)) {
            workbook.write(fileOut);
        } catch (IOException e) {
            throw new RuntimeException("파일 쓰기에 실패했습니다", e);
        }
    }

    private Sheet createSheet(Workbook workbook) {
        Sheet sheet = workbook.createSheet(SHEET_NAME);
        sheet.setDefaultColumnWidth(COLUMN_WIDTH);
        createHeaderRow(sheet);
        return sheet;
    }

    private void createHeaderRow(Sheet sheet) {
        Row headerRow = sheet.createRow(0);
        Cell headerCell = headerRow.createCell(0);
        headerCell.setCellValue("시작 모듈 ↓");
    }

    private void createRecursiveCell(
        String functionName, int depth, int rowIndex, ChildPosition position) {
        Callee recursiveCallee = new Callee(functionName + RECURSIVE_NODE_POSTFIX,
            CalleeType.FUNCTION);
        createCell(depth, rowIndex, position, recursiveCallee);
    }

    private SXSSFWorkbook getWorkbook() {
        SXSSFWorkbook workbook = new SXSSFWorkbook(1000);
        workbook.setCompressTempFiles(true);
        return workbook;
    }

    @Override
    public void close() throws Exception {
        workbook.close();
    }
}
