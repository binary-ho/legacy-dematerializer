package binary.ho.graph;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import binary.ho.function.Function;
import binary.ho.function.callee.Callee;
import binary.ho.function.callee.CalleeType;
import binary.ho.module.CModule;
import binary.ho.module.CModules;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class CallGraphSearcherTest {

    @Mock
    private CModules cModules;

    @Mock
    private CModule module;

    @Mock
    private Function function;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("모듈 이름에서 콜 그래프를 검색한다")
    void searchFromModuleName() {
        // given
        String moduleName = "testModule";
        when(cModules.get(moduleName)).thenReturn(module);
        when(module.getRepresentativeFunction()).thenReturn(function);
        when(function.getName()).thenReturn("testFunction");
        when(module.isExternalCall("testFunction")).thenReturn(false);
        when(module.getFunction("testFunction")).thenReturn(function);
        when(function.hasNoCallee()).thenReturn(true);

        // when
        Node result = CallGraphSearcher.searchFromModule(moduleName, cModules);

        // then
        assertNotNull(result);
        assertEquals("testFunction", result.getFunctionName());
        assertTrue(result.isLeaf());
    }

    @Test
    @DisplayName("함수 호출이 없는 함수를 검색하면 리프 노드를 반환한다")
    void searchFunctionWithNoCalleeReturnsLeafNode() {
        // given
        String moduleName = "testModule";
        when(cModules.get(moduleName)).thenReturn(module);
        when(module.getRepresentativeFunction()).thenReturn(function);
        when(function.getName()).thenReturn("testFunction");
        when(module.isExternalCall("testFunction")).thenReturn(false);
        when(module.getFunction("testFunction")).thenReturn(function);
        when(function.hasNoCallee()).thenReturn(true);

        // when
        Node result = CallGraphSearcher.searchFromModule(moduleName, cModules);

        // then
        assertTrue(result.isLeaf());
    }

    @Test
    @DisplayName("SQL 쿼리 호출을 포함하는 함수를 검색하면 SQL 노드를 생성한다")
    void searchFunctionWithSqlCalleeCreatesSqlNode() {
        // given
        String moduleName = "testModule";
        Callee sqlCallee = mock(Callee.class);
        when(sqlCallee.getType()).thenReturn(CalleeType.SQL);
        when(sqlCallee.getName()).thenReturn("SELECT * FROM test");

        when(cModules.get(moduleName)).thenReturn(module);
        when(module.getRepresentativeFunction()).thenReturn(function);
        when(function.getName()).thenReturn("testFunction");
        when(module.isExternalCall("testFunction")).thenReturn(false);
        when(module.getFunction("testFunction")).thenReturn(function);
        when(function.hasNoCallee()).thenReturn(false);
        when(function.getCallees()).thenReturn(List.of(sqlCallee));

        // when
        Node result = CallGraphSearcher.searchFromModule(moduleName, cModules);

        // then
        assertFalse(result.isLeaf());
        assertEquals(1, result.getNextNodes().size());
        assertEquals("(QUERY) SELECT * FROM test", result.getNextNodes().get(0).getFunctionName());
        assertTrue(result.getNextNodes().get(0).isLeaf());
    }

    @Test
    @DisplayName("함수가 자기 자신을 호출하는 재귀적 호출을 감지한다")
    void detectRecursiveFunctionCall() {
        // given
        String moduleName = "testModule";
        String functionName = "recursiveFunction";
        Callee selfCallee = mock(Callee.class);
        when(selfCallee.getType()).thenReturn(CalleeType.FUNCTION);
        when(selfCallee.getName()).thenReturn(functionName);

        when(cModules.get(moduleName)).thenReturn(module);
        when(module.getRepresentativeFunction()).thenReturn(function);
        when(function.getName()).thenReturn(functionName);
        when(module.isExternalCall(functionName)).thenReturn(false);
        when(module.getFunction(functionName)).thenReturn(function);
        when(function.hasNoCallee()).thenReturn(false);
        when(function.getCallees()).thenReturn(List.of(selfCallee));

        // when
        Node result = CallGraphSearcher.searchFromModule(moduleName, cModules);

        // then
        assertFalse(result.isLeaf());
        assertEquals(1, result.getNextNodes().size());
        assertTrue(result.getNextNodes().get(0).getFunctionName().contains("(RECURSIVE)"));
        assertTrue(result.getNextNodes().get(0).isLeaf());
    }
}
