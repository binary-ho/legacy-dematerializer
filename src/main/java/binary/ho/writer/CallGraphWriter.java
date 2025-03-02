package binary.ho.writer;

import binary.ho.graph.Node;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class CallGraphWriter {

    private final Node rootNode;
    private final Workbook workbook;
    private final Sheet sheet;
    private final DepthRowIndex depthRowIndex;

    private static final int START_DEPTH = 0;
    private static final int START_ROW_INDEX = 1;
    private static final int COLUMN_WIDTH = 40;
    private static final String ARROW = "→ ";

    private CallGraphWriter(Node rootNode) {
        this.rootNode = rootNode;
        this.workbook = new XSSFWorkbook();
        this.sheet = createSheet(rootNode, workbook);
        this.depthRowIndex = new DepthRowIndex();
    }

    public static void write(Node rootNode, String outputPath) throws IOException {
        CallGraphWriter writer = new CallGraphWriter(rootNode);
        writer.writeFromRootNode();
        writer.writeToFile(outputPath);
    }

    private void writeFromRootNode() {
        depthRowIndex.setNextRow(START_DEPTH, START_ROW_INDEX);
        writeNode(rootNode, START_DEPTH, START_ROW_INDEX, false);
    }

    private int writeNode(Node node, int depth, int rowIndex, boolean hasParent) {
        Row row = getRow(rowIndex);

        Cell cell = row.createCell(depth);
        String cellValue = getValue(node.getFunctionName(), hasParent);
        cell.setCellValue(cellValue);

        if (node.isLeaf()) {
            depthRowIndex.setNextRow(depth, rowIndex + 1);
            return rowIndex + 1;
        }
        depthRowIndex.setNextRow(depth + 1, rowIndex);

        for (Node child : node.getNextNodes()) {
            int nextRowIndex = depthRowIndex.getNextRow(depth + 1);
            int newNextRowIndex = writeNode(child, depth + 1, nextRowIndex, true);
            depthRowIndex.setNextRow(depth + 1, newNextRowIndex);
        }

        depthRowIndex.setNextRow(depth, rowIndex + 1);
        return depthRowIndex.getMaxRowBelowDepth(depth);
    }

    private Row getRow(int currentRow) {
        Row row = sheet.getRow(currentRow);
        if (row != null) {
            return row;
        }
        return sheet.createRow(currentRow);
    }

    private String getValue(String functionName, boolean hasParent) {
        if (hasParent) {
            return ARROW + functionName;
        }
        return functionName;
    }

    private void writeToFile(String outputPath) throws IOException {
        try (FileOutputStream fileOut = new FileOutputStream(outputPath)) {
            workbook.write(fileOut);
        } finally {
            workbook.close();
        }
    }

    private Sheet createSheet(Node rootNode, Workbook workbook) {
        String sheetName = rootNode.getFunctionName();
        Sheet sheet = workbook.createSheet(sheetName);
        sheet.setDefaultColumnWidth(COLUMN_WIDTH);
        createHeaderRow(sheet);
        return sheet;
    }

    private void createHeaderRow(Sheet sheet) {
        Row headerRow = sheet.createRow(0);
        Cell headerCell = headerRow.createCell(0);
        headerCell.setCellValue("시작 모듈: " + sheet.getSheetName());
    }
}
