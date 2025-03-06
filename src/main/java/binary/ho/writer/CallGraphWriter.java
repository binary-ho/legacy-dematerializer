package binary.ho.writer;

import binary.ho.graph.Node;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
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
    private final CellCreator cellCreator;

    private static final int START_DEPTH = 0;
    private static final int START_ROW_INDEX = 1;
    private static final int COLUMN_WIDTH = 50;
    private static final int ROW_HEIGHT = 350;
    private static final String TREE_VERTICAL = " │";

    private CallGraphWriter(Node rootNode) {
        this.rootNode = rootNode;
        this.workbook = new XSSFWorkbook();
        this.sheet = createSheet(rootNode, workbook);
        this.depthRowIndex = new DepthRowIndex();
        this.cellCreator = new CellCreator(workbook);
    }

    public static void write(Node rootNode, String outputPath) throws IOException {
        CallGraphWriter writer = new CallGraphWriter(rootNode);
        writer.writeFromRootNode();
        writer.writeToFile(outputPath);
    }

    private void writeFromRootNode() {
        depthRowIndex.setNextRow(START_DEPTH, START_ROW_INDEX);
        writeNode(rootNode, START_DEPTH, START_ROW_INDEX, ChildPosition.NO_PARENT);
    }

    private int writeNode(Node node, int depth, int rowIndex, ChildPosition position) {
        Row row = getRow(rowIndex);
        row.setHeight((short) ROW_HEIGHT);

        cellCreator.createFromCallee(row, depth, node.getCallee(), position);

        if (node.isLeaf()) {
            depthRowIndex.setNextRow(depth, rowIndex + 1);
            return rowIndex + 1;
        }
        depthRowIndex.setNextRow(depth + 1, rowIndex);

        List<Node> nextNodes = node.getNextNodes();
        for (int index = 0; index < nextNodes.size(); index++) {
            Node child = nextNodes.get(index);
            int nextRow = depthRowIndex.getNextRow(depth + 1);
            ChildPosition nextPosition = ChildPosition.getPosition(index, nextNodes);

            int nextDepthRow = writeNode(child, depth + 1, nextRow, nextPosition);
            depthRowIndex.setNextRow(depth + 1, nextDepthRow);

            if (isLast(index, nextNodes)) {
                connectNodes(depth + 1, rowIndex, nextRow);
            }
        }

        depthRowIndex.setNextRow(depth, rowIndex + 1);
        return depthRowIndex.getMaxRowBelowDepth(depth) + 1;
    }

    private boolean isLast(int index, List<Node> nextNodes) {
        return index == nextNodes.size() - 1;
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

    private void writeToFile(String outputPath) throws IOException {
        try (FileOutputStream fileOut = new FileOutputStream(outputPath)) {
            workbook.write(fileOut);
        } finally {
            workbook.close();
        }
    }

    private Sheet createSheet(Node rootNode, Workbook workbook) {
        String sheetName = rootNode.getCallee().getName();
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
