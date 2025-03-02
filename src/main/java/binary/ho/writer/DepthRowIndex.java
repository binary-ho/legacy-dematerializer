package binary.ho.writer;

import java.util.HashMap;
import java.util.Map;

public class DepthRowIndex {

    private final Map<Integer, Integer> depthToRow;

    public DepthRowIndex() {
        this.depthToRow = new HashMap<>();
    }

    public void setNextRow(int depth, int rowIndex) {
        depthToRow.put(depth, rowIndex);
    }

    public int getNextRow(int depth) {
        return depthToRow.getOrDefault(depth, 0);
    }

    public int getMaxRowBelowDepth(int depth) {
        int maxRow = depthToRow.get(depth + 1);
        for (int i = depth + 2; i < depthToRow.size() + depth; i++) {
            if (depthToRow.containsKey(i)) {
                maxRow = Math.max(maxRow, depthToRow.get(i));
            }
        }
        return maxRow;
    }
}
