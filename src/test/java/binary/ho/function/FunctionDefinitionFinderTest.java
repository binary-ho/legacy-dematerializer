package binary.ho.function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import binary.ho.config.TestConfigManager;
import binary.ho.query.ProC_QueryParser;
import binary.ho.query.ProC_QueryRemover;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class FunctionDefinitionFinderTest {

    @Mock
    private FunctionCallFinder functionCallFinder;

    @Mock
    private ProC_QueryParser proC_queryParser;

    @Mock
    private ProC_QueryRemover proC_queryRemover;

    private TestConfigManager testConfigManager;
    private FunctionDefinitionFinder functionDefinitionFinder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testConfigManager = new TestConfigManager();
        functionDefinitionFinder = new FunctionDefinitionFinder(
            testConfigManager.getFunctionDefinitionPattern(),
            testConfigManager.getExcludeFunctions(),
            functionCallFinder,
            proC_queryParser,
            proC_queryRemover
        );
    }

    @Test
    @DisplayName("함수 정의를 찾을 수 있다")
    void hasNextReturnsTrueWhenFunctionFound() {
        // given
        String code = "void testFunction() { int a = 10; }";
        CodeSearchScope codeSearchScope = new CodeSearchScope(code);

        // when
        boolean hasNext = functionDefinitionFinder.hasNext(codeSearchScope);

        // then
        assertTrue(hasNext);
    }

    @Test
    @DisplayName("제외 목록에 있는 함수는 건너뛴다")
    void skipExcludedFunctions() {
        // given
        testConfigManager = new TestConfigManager();
        functionDefinitionFinder = new FunctionDefinitionFinder(
            testConfigManager.getFunctionDefinitionPattern(),
            Set.of("ExcludeFunction"),
            functionCallFinder,
            proC_queryParser,
            proC_queryRemover
        );

        String code = "int ExcludeFunction() { int a = 1; }\n"
            + "void Test1() { int a = 10; }\n"
            + "void ExcludeFunction() { int c = 30; }\n"
            + "void test3() { int b = 20; }\n"
            + "void Test2() { int b = 20; }\n";
        CodeSearchScope codeSearchScope = new CodeSearchScope(code);

        // when
        boolean hasNext = functionDefinitionFinder.hasNext(codeSearchScope);
        Function test1Function = functionDefinitionFinder.next(codeSearchScope);

        boolean hasNext2 = functionDefinitionFinder.hasNext(codeSearchScope);
        Function test2Function = functionDefinitionFinder.next(codeSearchScope);

        // then
        assertTrue(hasNext);
        assertEquals("Test1", test1Function.getName());
        assertEquals("Test2", test2Function.getName());
    }

    @Test
    @DisplayName("중첩된 중괄호를 올바르게 처리한다")
    void handleNestedBraces() {
        // given
        String code = "void testFunction() { if (condition) { doSomething(); } }";
        CodeSearchScope codeSearchScope = new CodeSearchScope(code);
        when(functionCallFinder.findAll(anyString(), anyString())).thenReturn(
            List.of("doSomething"));
        when(proC_queryParser.parse(anyString())).thenReturn(List.of());
        when(proC_queryRemover.removeSqlStatements(anyString())).thenReturn(
            " if (condition) { doSomething(); } ");

        // when
        boolean hasNext = functionDefinitionFinder.hasNext(codeSearchScope);
        Function function = functionDefinitionFinder.next(codeSearchScope);

        // then
        assertTrue(hasNext);
        assertEquals("testFunction", function.getName());
        assertEquals(1, function.getCallees().size());
        assertEquals("doSomething", function.getCallees().get(0).getName());
    }

    @Test
    @DisplayName("비정상적인 중괄호 패턴이 있는 경우 예외를 반환한다.")
    void handleMalformedBraces() {
        // given
        String code = "void malformedFunction() { int a = 10; // missing closing brace\nvoid validFunction() { return; }";
        CodeSearchScope codeSearchScope = new CodeSearchScope(code);
        when(functionCallFinder.findAll(anyString(), anyString())).thenReturn(List.of());
        when(proC_queryParser.parse(anyString())).thenReturn(List.of());
        when(proC_queryRemover.removeSqlStatements(anyString())).thenReturn(" return; ");

        // when
        // then
        assertThrows(IllegalStateException.class,
            () -> functionDefinitionFinder.next(codeSearchScope));
    }

    @Test
    @DisplayName("더 이상 함수가 없을 때 hasNext는 false를 반환한다")
    void hasNextReturnsFalseWhenNoMoreFunctions() {
        // given
        String code = "int a = 10;";
        CodeSearchScope codeSearchScope = new CodeSearchScope(code);

        // when
        boolean hasNext = functionDefinitionFinder.hasNext(codeSearchScope);

        // then
        assertFalse(hasNext);
    }

    @Test
    @DisplayName("hasNext가 false일 때 next를 호출하면 예외가 발생한다")
    void nextThrowsExceptionWhenNoMoreFunctions() {
        // given
        String code = "int a = 10;";
        CodeSearchScope codeSearchScope = new CodeSearchScope(code);

        // when & then
        assertFalse(functionDefinitionFinder.hasNext(codeSearchScope));
        assertThrows(IllegalStateException.class,
            () -> functionDefinitionFinder.next(codeSearchScope));
    }

    @Test
    @DisplayName("함수 정의를 찾고 함수 본문에서 함수 호출을 추출한다")
    void extractFunctionCallsFromFunctionBody() {
        // given
        String code = "void testFunction() { helper1(); helper2(); }";
        CodeSearchScope codeSearchScope = new CodeSearchScope(code);
        when(functionCallFinder.findAll(anyString(), anyString())).thenReturn(
            List.of("helper1", "helper2"));
        when(proC_queryParser.parse(anyString())).thenReturn(List.of());
        when(proC_queryRemover.removeSqlStatements(anyString())).thenReturn(
            " helper1(); helper2(); ");

        // when
        boolean hasNext = functionDefinitionFinder.hasNext(codeSearchScope);
        assertTrue(hasNext);

        Function function = functionDefinitionFinder.next(codeSearchScope);

        // then
        assertEquals("testFunction", function.getName());
        assertEquals(2, function.getCallees().size());
        assertEquals("helper1", function.getCallees().get(0).getName());
        assertEquals("helper2", function.getCallees().get(1).getName());
    }
}
