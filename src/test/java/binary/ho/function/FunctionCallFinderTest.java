package binary.ho.function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import binary.ho.config.TestConfigManager;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FunctionCallFinderTest {

    private FunctionCallFinder functionCallFinder;

    @BeforeEach
    void setUp() {
        TestConfigManager testConfigManager = new TestConfigManager();
        functionCallFinder = new FunctionCallFinder(
            testConfigManager.getFunctionCallPattern(),
            testConfigManager.getExcludeFunctions()
        );
    }

    @Test
    @DisplayName("함수 본문에서 대문자가 최소 1글자 포함된 함수 호출을 찾을 수 있다")
    void findFunctionCalls() {
        // given
        String functionBody = "HELPER1(); helper2(); hel_per3(); Helper4(); HelPer5(); helPer6();";

        // when
        List<String> functionCalls = functionCallFinder.findAll("currentFunction", functionBody);

        // then
        assertEquals(4, functionCalls.size());
        assertTrue(functionCalls.contains("HELPER1"));
        assertFalse(functionCalls.contains("helper2"));
        assertFalse(functionCalls.contains("hel_per3"));
        assertTrue(functionCalls.contains("Helper4"));
        assertTrue(functionCalls.contains("HelPer5"));
        assertTrue(functionCalls.contains("helPer6"));
    }

    @Test
    @DisplayName("제외 목록에 있는 함수 호출은 건너뛴다")
    void skipExcludedFunctionCalls() {
        // given
        TestConfigManager testConfigManager = new TestConfigManager();
        functionCallFinder = new FunctionCallFinder(
            testConfigManager.getFunctionCallPattern(),
            Set.of("Helper1", "Helper3")
        );

        String functionBody = "Helper1(); printf(\"hello\"); Helper2(); Helper3(); scanf(\"%d\", &num);";

        // when
        List<String> functionCalls = functionCallFinder.findAll("currentFunction", functionBody);

        // then
        assertEquals(1, functionCalls.size());
        assertFalse(functionCalls.contains("Helper1"));
        assertTrue(functionCalls.contains("Helper2"));
        assertFalse(functionCalls.contains("Helper3"));
    }

    @Test
    @DisplayName("재귀적인 함수 호출은 포함하지 않는다")
    void skipRecursiveFunctionCalls() {
        // given
        String functionBody = "RecursiveFunction(n-1); Helper1(); RecursiveFunction(n-2); Helper2();";

        // when
        List<String> functionCalls = functionCallFinder.findAll("RecursiveFunction", functionBody);

        // then
        assertEquals(2, functionCalls.size());
        assertEquals("Helper1", functionCalls.get(0));
        assertEquals("Helper2", functionCalls.get(1));
        assertFalse(functionCalls.contains("RecursiveFunction"));
    }

    @Test
    @DisplayName("함수 호출이 없는 경우 빈 리스트를 반환한다")
    void returnEmptyListWhenNoFunctionCalls() {
        // given
        String functionBody = "int a = 10; if (a > 5) { return a; }";

        // when
        List<String> functionCalls = functionCallFinder.findAll("currentFunction", functionBody);

        // then
        functionCalls.forEach(System.out::println);
        assertTrue(functionCalls.isEmpty());
    }
}
