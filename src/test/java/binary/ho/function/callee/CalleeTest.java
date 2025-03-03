package binary.ho.function.callee;

import binary.ho.query.Query;
import binary.ho.query.SqlType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CalleeTest {

    @Test
    @DisplayName("함수 이름 리스트로부터 Callee 객체를 생성할 수 있다")
    void createFromFunctionNames() {
        // given
        List<String> functionNames = List.of("function1", "function2", "function3");

        // when
        List<Callee> callees = Callee.from(functionNames);

        // then
        assertEquals(3, callees.size());
        assertEquals("function1", callees.get(0).getName());
        assertEquals(CalleeType.FUNCTION, callees.get(0).getType());
        assertEquals("function2", callees.get(1).getName());
        assertEquals(CalleeType.FUNCTION, callees.get(1).getType());
        assertEquals("function3", callees.get(2).getName());
        assertEquals(CalleeType.FUNCTION, callees.get(2).getType());
    }

    @Test
    @DisplayName("Query 객체 리스트로부터 SQL 타입의 Callee 객체를 생성할 수 있다")
    void createFromQueries() {
        // given
        List<Query> queries = List.of(
            new Query(SqlType.SELECT),
            new Query(SqlType.INSERT),
            new Query(SqlType.UPDATE)
        );

        // when
        List<Callee> callees = Callee.fromQueries(queries);

        // then
        assertEquals(3, callees.size());
        assertEquals("SELECT", callees.get(0).getName());
        assertEquals(CalleeType.SQL, callees.get(0).getType());
        assertEquals("INSERT", callees.get(1).getName());
        assertEquals(CalleeType.SQL, callees.get(1).getType());
        assertEquals("UPDATE", callees.get(2).getName());
        assertEquals(CalleeType.SQL, callees.get(2).getType());
    }

    @Test
    @DisplayName("빈 리스트에 대해 빈 Callee 리스트를 반환한다")
    void createFromEmptyList() {
        // given
        List<String> emptyFunctionNames = List.of();
        List<Query> emptyQueries = List.of();

        // when
        List<Callee> calleesFromFunctions = Callee.from(emptyFunctionNames);
        List<Callee> calleesFromQueries = Callee.fromQueries(emptyQueries);

        // then
        assertTrue(calleesFromFunctions.isEmpty());
        assertTrue(calleesFromQueries.isEmpty());
    }
}
