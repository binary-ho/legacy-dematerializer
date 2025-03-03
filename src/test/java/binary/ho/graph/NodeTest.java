package binary.ho.graph;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NodeTest {

    @Test
    @DisplayName("리프 노드를 생성할 수 있다")
    void createLeafNode() {
        // when
        Node leafNode = Node.createLeafNode("testFunction");
        
        // then
        assertEquals("testFunction", leafNode.getFunctionName());
        assertTrue(leafNode.isLeaf());
        assertTrue(leafNode.getNextNodes().isEmpty());
    }
    
    @Test
    @DisplayName("다음 노드가 있는 일반 노드를 생성할 수 있다")
    void createNodeWithNextNodes() {
        // given
        Node child1 = Node.createLeafNode("child1");
        Node child2 = Node.createLeafNode("child2");
        List<Node> children = List.of(child1, child2);
        
        // when
        Node node = Node.createNode("parentFunction", children);
        
        // then
        assertEquals("parentFunction", node.getFunctionName());
        assertFalse(node.isLeaf());
        assertEquals(2, node.getNextNodes().size());
        assertEquals("child1", node.getNextNodes().get(0).getFunctionName());
        assertEquals("child2", node.getNextNodes().get(1).getFunctionName());
    }
    
    @Test
    @DisplayName("자식이 없는 노드는 리프 노드로 간주한다")
    void nodeWithNoChildrenIsLeaf() {
        // when
        Node node = Node.createNode("function", List.of());
        
        // then
        assertTrue(node.isLeaf());
    }
    
    @Test
    @DisplayName("노드의 기본 속성에 접근할 수 있다")
    void accessNodeProperties() {
        // given
        Node child = Node.createLeafNode("child");
        List<Node> children = List.of(child);
        
        // when
        Node node = Node.createNode("parentFunction", children);
        
        // then
        assertEquals("parentFunction", node.getFunctionName());
        assertFalse(node.isLeaf());
        assertEquals(1, node.getNextNodes().size());
        assertEquals("child", node.getNextNodes().get(0).getFunctionName());
    }
}
