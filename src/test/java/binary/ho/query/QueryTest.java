package binary.ho.query;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QueryTest {

    @Test
    @DisplayName("Query 객체는 SQL 타입을 갖는다")
    void queryHasSqlType() {
        // given
        SqlType sqlType = SqlType.SELECT;

        // when
        Query query = new Query(sqlType);

        // then
        assertEquals(sqlType, query.getType());
    }

    @Test
    @DisplayName("모든 SQL 타입으로 Query 객체를 생성할 수 있다")
    void createQueryWithAllSqlTypes() {
        // Tests for all SQL types
        for (SqlType sqlType : SqlType.values()) {
            // when
            Query query = new Query(sqlType);

            // then
            assertEquals(sqlType, query.getType());
        }
    }
}
