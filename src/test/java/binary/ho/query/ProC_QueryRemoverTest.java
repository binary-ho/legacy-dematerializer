package binary.ho.query;

import binary.ho.config.TestConfigManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class ProC_QueryRemoverTest {

    private ProC_QueryRemover proC_queryRemover;
    private TestConfigManager testConfigManager;

    @BeforeEach
    void setUp() {
        testConfigManager = new TestConfigManager();
        proC_queryRemover = new ProC_QueryRemover(testConfigManager.getProcSqlPattern());
    }

    @Test
    @DisplayName("코드에서 Pro*C SQL 문을 제거할 수 있다")
    void removeSqlStatementsFromCode() {
        // given
        String code = "void function() {\n" +
                      "    EXEC SQL SELECT * FROM employees;\n" +
                      "    process();\n" +
                      "    EXEC SQL INSERT INTO logs VALUES (:id);\n" +
                      "}";

        // when
        String result = proC_queryRemover.removeSqlStatements(code);

        // then
        String expected = "void function() {\n" +
                         "    \n" +
                         "    process();\n" +
                         "    \n" +
                         "}";
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("여러 줄에 걸친 Pro*C SQL 문을 제거할 수 있다")
    void removeMultilineSqlStatements() {
        // given
        String code = "void function() {\n" +
                      "    EXEC SQL \n" +
                      "        SELECT * \n" +
                      "        FROM employees \n" +
                      "        WHERE id = :id;\n" +
                      "    process();\n" +
                      "}";

        // when
        String result = proC_queryRemover.removeSqlStatements(code);

        // then
        String expected = "void function() {\n" +
                         "    \n" +
                         "    process();\n" +
                         "}";
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("SQL 문이 없는 코드는 그대로 반환한다")
    void returnUnchangedCodeWhenNoSqlStatements() {
        // given
        String code = "void function() {\n" +
                      "    int a = 10;\n" +
                      "    process();\n" +
                      "}";

        // when
        String result = proC_queryRemover.removeSqlStatements(code);

        // then
        assertEquals(code, result);
    }

    @Test
    @DisplayName("모든 SQL 문을 제거할 수 있다")
    void removeAllSqlStatements() {
        // given
        String code = "void function() {\n" +
                      "    EXEC SQL SELECT * FROM table1;\n" +
                      "    EXEC SQL INSERT INTO table2 VALUES (:a, :b);\n" +
                      "    EXEC SQL UPDATE table3 SET col = :val;\n" +
                      "    EXEC SQL DELETE FROM table4;\n" +
                      "    EXEC SQL COMMIT;\n" +
                      "    EXEC SQL ROLLBACK;\n" +
                      "}";

        // when
        String result = proC_queryRemover.removeSqlStatements(code);

        // then
        String expected = "void function() {\n" +
                         "    \n" +
                         "    \n" +
                         "    \n" +
                         "    \n" +
                         "    \n" +
                         "    \n" +
                         "}";
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("빈 문자열에 대해서는 빈 문자열을 반환한다")
    void returnEmptyStringForEmptyInput() {
        // given
        String code = "";

        // when
        String result = proC_queryRemover.removeSqlStatements(code);

        // then
        assertEquals("", result);
    }
}
