package binary.ho.function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import binary.ho.config.TestConfigManager;
import binary.ho.function.callee.Callee;
import binary.ho.function.callee.CalleeType;
import binary.ho.query.ProC_QueryParser;
import binary.ho.query.ProC_QueryRemover;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class FunctionParserTest {

    @Mock
    private FunctionDefinitionFinder functionDefinitionFinder;

    private FunctionParser functionParser;
    private TestConfigManager testConfigManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        functionParser = new FunctionParser(functionDefinitionFinder);
        testConfigManager = new TestConfigManager();
    }

    @Test
    @DisplayName("코드에서 함수를 파싱할 수 있다")
    void parseFunctionsFromCode() {
        // given
        String code = "void function1() { }\nvoid function2() { }";
        Function function1 = Function.create("function1", List.of(), List.of());
        Function function2 = Function.create("function2", List.of(), List.of());

        when(functionDefinitionFinder.hasNext(any(CodeSearchScope.class)))
            .thenReturn(true, true, false);
        when(functionDefinitionFinder.next(any(CodeSearchScope.class)))
            .thenReturn(function1, function2);

        // when
        List<Function> functions = functionParser.parse(code);

        // then
        assertEquals(2, functions.size());
        assertEquals(function1, functions.get(0));
        assertEquals(function2, functions.get(1));

        // 주요 검증: 문자열과 주석이 제거된 코드가 전달되는지 확인
        verify(functionDefinitionFinder, times(3)).hasNext(any(CodeSearchScope.class));
        verify(functionDefinitionFinder, times(2)).next(any(CodeSearchScope.class));
    }

    @Test
    @DisplayName("코드에 함수가 없는 경우 빈 리스트를 반환한다")
    void parseEmptyCodeReturnsEmptyList() {
        // given
        String code = "int a = 10;";
        when(functionDefinitionFinder.hasNext(any(CodeSearchScope.class)))
            .thenReturn(false);

        // when
        List<Function> functions = functionParser.parse(code);

        // then
        assertTrue(functions.isEmpty());
        verify(functionDefinitionFinder, times(1)).hasNext(any(CodeSearchScope.class));
        verify(functionDefinitionFinder, never()).next(any(CodeSearchScope.class));
    }

    @Test
    @DisplayName("통합 테스트 - 실제 코드에서 함수 파싱")
    void integrationTestWithRealParser() {
        // given - 실제 구현체를 생성
        Pattern functionDefinitionPattern = testConfigManager.getFunctionDefinitionPattern();
        Set<String> excludeFunctions = testConfigManager.getExcludeFunctions();
        Pattern functionCallPattern = testConfigManager.getFunctionCallPattern();
        Pattern proCSqlPattern = testConfigManager.getProcSqlPattern();

        FunctionCallFinder functionCallFinder = new FunctionCallFinder(functionCallPattern,
            excludeFunctions);
        ProC_QueryParser proC_queryParser = new ProC_QueryParser(proCSqlPattern);
        ProC_QueryRemover proC_queryRemover = new ProC_QueryRemover(proCSqlPattern);

        FunctionDefinitionFinder realFinder = new FunctionDefinitionFinder(
            functionDefinitionPattern,
            excludeFunctions,
            functionCallFinder,
            proC_queryParser,
            proC_queryRemover
        );

        FunctionParser realParser = new FunctionParser(realFinder);

        // when - 간단한 C 코드를 파싱
        String code =
            "void functionA() {\n" +
                "    functionB();\n" +
                "}\n" +
                "\n" +
                "void functionB() {\n" +
                "    printf(\"Hello\");\n" +
                "}";

        List<Function> functions = realParser.parse(code);

        // then - 두 함수가 파싱되고 올바른 호출 관계가 있어야 함
        assertEquals(2, functions.size());

        Function function1 = functions.stream()
            .filter(f -> f.getName().equals("functionA"))
            .findFirst()
            .orElseThrow();

        Function function2 = functions.stream()
            .filter(f -> f.getName().equals("functionB"))
            .findFirst()
            .orElseThrow();

        // function1이 function2를 호출하는지 확인
        assertEquals(1, function1.getCallees().size());
        assertEquals("functionB", function1.getCallees().get(0).getName());

        // function2는 제외된 함수(printf)만 호출하므로 빈 리스트여야 함
        assertTrue(function2.hasNoCallee());
    }

    @Test
    @DisplayName("ProC 코드 파싱 시 SQL 쿼리를 인식한다")
    void parseProCCodeWithSqlQueries() {
        // given - 실제 구현체를 생성
        Pattern functionDefinitionPattern = testConfigManager.getFunctionDefinitionPattern();
        Set<String> excludeFunctions = testConfigManager.getExcludeFunctions();
        Pattern functionCallPattern = testConfigManager.getFunctionCallPattern();
        Pattern proCSqlPattern = testConfigManager.getProcSqlPattern();

        FunctionCallFinder functionCallFinder = new FunctionCallFinder(functionCallPattern,
            excludeFunctions);
        ProC_QueryParser proC_queryParser = new ProC_QueryParser(proCSqlPattern);
        ProC_QueryRemover proC_queryRemover = new ProC_QueryRemover(proCSqlPattern);

        FunctionDefinitionFinder realFinder = new FunctionDefinitionFinder(
            functionDefinitionPattern,
            excludeFunctions,
            functionCallFinder,
            proC_queryParser,
            proC_queryRemover
        );

        FunctionParser realParser = new FunctionParser(realFinder);

        // when - ProC 코드를 파싱
        String code =
            "void fetchData() {\n" +
                "    EXEC SQL SELECT id FROM employees;\n" +
                "    processData();\n" +
                "    EXEC SQL INSERT INTO logs VALUES (:id);\n" +
                "}";

        List<Function> functions = realParser.parse(code);

        // then
        assertEquals(1, functions.size());
        Function fetchData = functions.get(0);
        assertEquals("fetchData", fetchData.getName());

        // 함수 호출과 SQL 쿼리 모두 포함
        assertEquals(3, fetchData.getCallees().size());

        // SQL 쿼리와 함수 호출이 모두 있는지 확인
        boolean hasSelectQuery = false;
        boolean hasInsertQuery = false;
        boolean hasProcessDataCall = false;

        for (Callee callee : fetchData.getCallees()) {
            if (callee.getType() == CalleeType.SQL) {
                if (callee.getName().equals("SELECT")) {
                    hasSelectQuery = true;
                }
                if (callee.getName().equals("INSERT")) {
                    hasInsertQuery = true;
                }
            } else if (callee.getName().equals("processData")) {
                hasProcessDataCall = true;
            }
        }

        assertTrue(hasSelectQuery, "SELECT 쿼리가 인식되어야 함");
        assertTrue(hasInsertQuery, "INSERT 쿼리가 인식되어야 함");
        assertTrue(hasProcessDataCall, "processData 함수 호출이 인식되어야 함");
    }
}
