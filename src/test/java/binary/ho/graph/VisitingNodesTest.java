package binary.ho.graph;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VisitingNodesTest {

    private VisitingNodes visitingNodes;

    @BeforeEach
    void setUp() {
        visitingNodes = new VisitingNodes();
    }

    @Test
    @DisplayName("노드를 방문 상태로 표시하고 확인할 수 있다")
    void markNodeAsVisitingAndCheck() {
        // given
        String functionName = "testFunction";
        
        // when
        visitingNodes.visit(functionName);
        
        // then
        assertTrue(visitingNodes.isVisiting(functionName));
    }
    
    @Test
    @DisplayName("방문하지 않은 노드는 방문 상태가 아니다")
    void unvisitedNodeIsNotVisiting() {
        // given
        String functionName = "testFunction";
        
        // then
        assertFalse(visitingNodes.isVisiting(functionName));
    }
    
    @Test
    @DisplayName("방문 상태에서 나갈 수 있다")
    void exitFromVisitingState() {
        // given
        String functionName = "testFunction";
        visitingNodes.visit(functionName);
        assertTrue(visitingNodes.isVisiting(functionName));
        
        // when
        visitingNodes.exit(functionName);
        
        // then
        assertFalse(visitingNodes.isVisiting(functionName));
    }
    
    @Test
    @DisplayName("MISSING과 같은 제외된 노드는 방문 상태로 표시되지 않는다")
    void excludedNodesAreNotMarkedAsVisiting() {
        // given
        String excludedFunction = "MISSING";
        
        // when
        visitingNodes.visit(excludedFunction);
        
        // then
        assertFalse(visitingNodes.isVisiting(excludedFunction));
    }
    
    @Test
    @DisplayName("여러 노드의 방문 상태를 독립적으로 관리한다")
    void manageMultipleNodesIndependently() {
        // given
        String function1 = "function1";
        String function2 = "function2";
        
        // when
        visitingNodes.visit(function1);
        
        // then
        assertTrue(visitingNodes.isVisiting(function1));
        assertFalse(visitingNodes.isVisiting(function2));
        
        // when
        visitingNodes.visit(function2);
        
        // then
        assertTrue(visitingNodes.isVisiting(function1));
        assertTrue(visitingNodes.isVisiting(function2));
        
        // when
        visitingNodes.exit(function1);
        
        // then
        assertFalse(visitingNodes.isVisiting(function1));
        assertTrue(visitingNodes.isVisiting(function2));
    }
}
