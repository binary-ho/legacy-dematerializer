package binary.ho.writer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DepthRowIndexTest {

    private DepthRowIndex depthRowIndex;

    @BeforeEach
    void setUp() {
        depthRowIndex = new DepthRowIndex();
    }

    @Test
    @DisplayName("특정 깊이의 다음 행 인덱스를 설정하고 가져올 수 있다")
    void setAndGetNextRow() {
        // given
        int depth = 2;
        int rowIndex = 5;
        
        // when
        depthRowIndex.setNextRow(depth, rowIndex);
        
        // then
        assertEquals(rowIndex, depthRowIndex.getNextRow(depth));
    }
    
    @Test
    @DisplayName("설정되지 않은 깊이의 다음 행 인덱스는 기본값 0을 반환한다")
    void getDefaultRowIndexForUnsetDepth() {
        // given
        int depth = 3;
        
        // when
        int rowIndex = depthRowIndex.getNextRow(depth);
        
        // then
        assertEquals(0, rowIndex);
    }
    
    @Test
    @DisplayName("여러 깊이의 행 인덱스를 설정하고 가져올 수 있다")
    void setAndGetMultipleDepthRowIndices() {
        // given
        depthRowIndex.setNextRow(1, 10);
        depthRowIndex.setNextRow(2, 15);
        depthRowIndex.setNextRow(3, 20);
        
        // then
        assertEquals(10, depthRowIndex.getNextRow(1));
        assertEquals(15, depthRowIndex.getNextRow(2));
        assertEquals(20, depthRowIndex.getNextRow(3));
    }
    
    @Test
    @DisplayName("특정 깊이 이하의 최대 행 인덱스를 가져올 수 있다")
    void getMaxRowBelowDepth() {
        // given
        depthRowIndex.setNextRow(1, 10);
        depthRowIndex.setNextRow(2, 15);
        depthRowIndex.setNextRow(3, 20);
        depthRowIndex.setNextRow(4, 12);
        
        // when
        int maxRowBelow1 = depthRowIndex.getMaxRowBelowDepth(1);
        
        // then
        assertEquals(20, maxRowBelow1);
    }
    
    @Test
    @DisplayName("특정 깊이의 행 인덱스를 업데이트할 수 있다")
    void updateRowIndexForExistingDepth() {
        // given
        depthRowIndex.setNextRow(1, 10);
        assertEquals(10, depthRowIndex.getNextRow(1));
        
        // when
        depthRowIndex.setNextRow(1, 15);
        
        // then
        assertEquals(15, depthRowIndex.getNextRow(1));
    }
    
    @Test
    @DisplayName("깊이에 따라 최대 행 인덱스 계산이 올바르게 동작한다")
    void maxRowCalculationWorksCorrectly() {
        // given
        depthRowIndex.setNextRow(1, 5);
        depthRowIndex.setNextRow(2, 10);
        depthRowIndex.setNextRow(3, 7);
        depthRowIndex.setNextRow(4, 15);
        
        // when & then
        assertEquals(15, depthRowIndex.getMaxRowBelowDepth(1)); // 2, 3, 4 중 최대 값
        assertEquals(15, depthRowIndex.getMaxRowBelowDepth(2)); // 3, 4 중 최대 값
        assertEquals(15, depthRowIndex.getMaxRowBelowDepth(3)); // 4의 값
    }
}
