package binary.ho.function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import binary.ho.function.callee.Callee;
import binary.ho.function.callee.CalleeType;
import binary.ho.query.Query;
import binary.ho.query.SqlType;
import binary.ho.query.table.Table;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FunctionTest {

    @Test
    @DisplayName("함수 생성 시 이름과 빈 호출 리스트를 가진다")
    void createFunction() {
        // given
        String functionName = "testFunction";
        List<Query> queries = List.of();
        List<String> calleeNames = List.of();

        // when
        Function function = Function.create(functionName, queries, calleeNames);

        // then
        assertEquals(functionName, function.getName());
        assertTrue(function.getCallees().isEmpty());
        assertTrue(function.hasNoCallee());
    }

    @Test
    @DisplayName("함수 생성 시 함수 호출 리스트가 제대로 변환된다")
    void createFunctionWithFunctionCallees() {
        // given
        String functionName = "testFunction";
        List<Query> queries = List.of();
        List<String> calleeNames = List.of("callee1", "callee2");

        // when
        Function function = Function.create(functionName, queries, calleeNames);

        // then
        assertEquals(functionName, function.getName());
        assertEquals(2, function.getCallees().size());

        function.getCallees().forEach(callee -> {
            assertTrue(calleeNames.contains(callee.getName()));
            assertEquals(CalleeType.FUNCTION, callee.getType());
        });

        assertFalse(function.hasNoCallee());
    }

    @Test
    @DisplayName("함수 생성 시 SQL 쿼리 호출 리스트가 제대로 변환된다")
    void createFunctionWithSqlQueries() {
        // given
        String functionName = "testFunction";
        List<Query> queries = List.of(
            new Query(SqlType.SELECT, Table.create(""), ""),
            new Query(SqlType.INSERT, Table.create(""), "")
        );
        List<String> calleeNames = List.of();

        // when
        Function function = Function.create(functionName, queries, calleeNames);

        // then
        assertEquals(functionName, function.getName());
        assertEquals(2, function.getCallees().size());

        Callee callee1 = function.getCallees().get(0);
        Callee callee2 = function.getCallees().get(1);

        assertTrue(callee1.getName().contains(SqlType.SELECT.name()));
        assertEquals(CalleeType.SQL, callee1.getType());

        assertTrue(callee2.getName().contains(SqlType.INSERT.name()));
        assertEquals(CalleeType.SQL, callee2.getType());

        assertFalse(function.hasNoCallee());
    }

    @Test
    @DisplayName("함수 생성 시 함수와 SQL 쿼리 호출 리스트 모두 포함된다")
    void createFunctionWithBothCallees() {
        // given
        String functionName = "testFunction";
        List<Query> queries = List.of(new Query(SqlType.SELECT, Table.create(""), ""));
        List<String> calleeNames = List.of("callee1");

        // when
        Function function = Function.create(functionName, queries, calleeNames);

        // then
        assertEquals(functionName, function.getName());
        assertEquals(2, function.getCallees().size());

        boolean hasFunctionCallee = false;
        boolean hasSqlCallee = false;

        for (Callee callee : function.getCallees()) {
            if (callee.getType() == CalleeType.FUNCTION && callee.getName().contains("callee1")) {
                hasFunctionCallee = true;
            }
            if (callee.getType() == CalleeType.SQL && callee.getName().contains("SELECT")) {
                hasSqlCallee = true;
            }
        }

        assertTrue(hasFunctionCallee, "함수 호출이 포함되어야 합니다");
        assertTrue(hasSqlCallee, "SQL 쿼리 호출이 포함되어야 합니다");
        assertFalse(function.hasNoCallee());
    }

    @Test
    @DisplayName("같은 이름을 가진 함수는 동일하다")
    void testEquals() {
        // given
        Function function1 = Function.create("sameFunction", List.of(), List.of("callee1"));
        Function function2 = Function.create("sameFunction",
            List.of(new Query(SqlType.SELECT, Table.create(""), "")),
            List.of());

        // when & then
        assertEquals(function1, function2);
        assertEquals(function1.hashCode(), function2.hashCode());
    }

    @Test
    @DisplayName("서로 다른 이름을 가진 함수는 동일하지 않다")
    void testNotEquals() {
        // given
        Function function1 = Function.create("function1", List.of(), List.of());
        Function function2 = Function.create("function2", List.of(), List.of());

        // when & then
        assertNotEquals(function1, function2);
        assertNotEquals(function1.hashCode(), function2.hashCode());
    }

    @Test
    @DisplayName("getCallees는 방어적 복사본을 반환한다")
    void testGetCalleesDefensiveCopy() {
        // given
        Function function = Function.create("testFunction", List.of(), List.of("callee1"));
        List<Callee> callees = function.getCallees();
        int originalSize = callees.size();

        // when & then
        assertThrows(UnsupportedOperationException.class, () -> callees.add(null));
        assertEquals(originalSize, function.getCallees().size());
    }
}
