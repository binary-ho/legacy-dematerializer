package binary.ho.query;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

class SqlTypeTest {

    @ParameterizedTest
    @CsvSource({
        "EXEC SQL SELECT * FROM employees, SELECT",
        "EXEC SQL INSERT INTO employees VALUES (1 'John'), INSERT",
        "EXEC SQL UPDATE employees SET name = 'John' WHERE id = 1, UPDATE",
        "EXEC SQL DELETE FROM employees WHERE id = 1, DELETE",
//        "EXEC SQL OPEN employee_cursor, OPEN",
//        "EXEC SQL FETCH employee_cursor INTO :id :name, FETCH",
//        "EXEC SQL FETCH employee_cursor INTO :id :name, FETCH",
//        "EXEC SQL CLOSE employee_cursor, CLOSE",
//        "EXEC SQL COMMIT, COMMIT",
//        "EXEC SQL ROLLBACK, ROLLBACK",
        "EXEC SQL CREATE TABLE employees, CREATE",
        "EXEC SQL ALTER TABLE employees ADD COLUMN, ALTER",
        "EXEC SQL DROP TABLE employees, DROP",
        "EXEC SQL TRUNCATE TABLE employees, TRUNCATE",
        "EXEC SQL MERGE INTO target USING source, MERGE"
    })
    @DisplayName("쿼리 문자열에서 올바른 SQL 타입을 추출한다")
    void extractCorrectSqlTypeFromQueryString(String query, String expectedType) {
        // when
        SqlType sqlType = SqlType.fromString(query);

        // then
        assertEquals(SqlType.valueOf(expectedType), sqlType);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("null 또는 빈 문자열에서는 기본값인 QUERY를 반환한다")
    void defaultToQueryForNullOrEmptyString(String query) {
        // when
        SqlType sqlType = SqlType.fromString(query);

        // then
        assertEquals(SqlType.NOT_FOUND, sqlType);
    }

    @Test
    @DisplayName("SQL 키워드가 없는 문자열에서는 기본값인 QUERY를 반환한다")
    void defaultToQueryForStringWithoutSqlKeyword() {
        // given
        String query = "some random string without sql keywords";

        // when
        SqlType sqlType = SqlType.fromString(query);

        // then
        assertEquals(SqlType.NOT_FOUND, sqlType);
    }

    @Test
    @DisplayName("첫 번째로 매칭되는 키워드를 기준으로 SQL 타입을 결정한다")
    void useFirstMatchingKeyword() {
        // given
        String query = "SELECT * FROM employees WHERE id IN (SELECT id FROM managers)";

        // when
        SqlType sqlType = SqlType.fromString(query);

        // then
        assertEquals(SqlType.SELECT, sqlType);
    }

    @Test
    @DisplayName("소문자로 작성된 키워드도 인식할 수 있다.")
    void onlyRecognizeUppercaseKeywords() {
        // given
        String query = "select * from employees INSERT";

        // when
        SqlType sqlType = SqlType.fromString(query);

        // then
        assertEquals(SqlType.SELECT, sqlType);
    }

    @Test
    @DisplayName("toString 메서드는 이름을 반환한다")
    void toStringReturnsName() {
        for (SqlType sqlType : SqlType.values()) {
            assertEquals(sqlType.name(), sqlType.toString());
        }
    }
}
