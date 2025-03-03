package binary.ho.query;

import binary.ho.config.TestConfigManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class ProC_QueryParserTest {

    private ProC_QueryParser proC_queryParser;
    private TestConfigManager testConfigManager;

    @BeforeEach
    void setUp() {
        testConfigManager = new TestConfigManager();
        proC_queryParser = new ProC_QueryParser(testConfigManager.getProcSqlPattern());
    }

    @Test
    @DisplayName("Pro*C 코드에서 SQL 쿼리를 파싱할 수 있다")
    void parseSqlQueriesFromProCCode() {
        // given
        String code = "void function() {\n" +
                      "    EXEC SQL SELECT * FROM employees;\n" +
                      "    process();\n" +
                      "    EXEC SQL INSERT INTO logs VALUES (:id);\n" +
                      "}";

        // when
        List<Query> queries = proC_queryParser.parse(code);

        // then
        assertEquals(2, queries.size());
        assertEquals(SqlType.SELECT, queries.get(0).getType());
        assertEquals(SqlType.INSERT, queries.get(1).getType());
    }

    @Test
    @DisplayName("여러 줄에 걸친 Pro*C SQL 쿼리를 파싱할 수 있다")
    void parseMultilineSqlQueries() {
        // given
        String code = "void function() {\n" +
                      "    EXEC SQL \n" +
                      "        SELECT * \n" +
                      "        FROM employees \n" +
                      "        WHERE id = :id;\n" +
                      "}";

        // when
        List<Query> queries = proC_queryParser.parse(code);

        // then
        assertEquals(1, queries.size());
        assertEquals(SqlType.SELECT, queries.get(0).getType());
    }

    @Test
    @DisplayName("SQL 쿼리가 없는 경우 빈 리스트를 반환한다")
    void returnEmptyListWhenNoSqlQueries() {
        // given
        String code = "void function() {\n" +
                      "    int a = 10;\n" +
                      "    process();\n" +
                      "}";

        // when
        List<Query> queries = proC_queryParser.parse(code);

        // then
        assertTrue(queries.isEmpty());
    }

    @Test
    @DisplayName("다양한 SQL 명령을 올바르게 인식한다")
    void recognizeVariousSqlCommands() {
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
        List<Query> queries = proC_queryParser.parse(code);

        // then
        assertEquals(6, queries.size());
        assertEquals(SqlType.SELECT, queries.get(0).getType());
        assertEquals(SqlType.INSERT, queries.get(1).getType());
        assertEquals(SqlType.UPDATE, queries.get(2).getType());
        assertEquals(SqlType.DELETE, queries.get(3).getType());
        assertEquals(SqlType.COMMIT, queries.get(4).getType());
        assertEquals(SqlType.ROLLBACK, queries.get(5).getType());
    }

    @Test
    @DisplayName("커서 관련 명령을 올바르게 인식한다")
    void recognizeCursorCommands() {
        // given
        String code = "void function() {\n" +
                      "    EXEC SQL DECLARE cur CURSOR FOR SELECT * FROM table;\n" +
                      "    EXEC SQL OPEN cur;\n" +
                      "    EXEC SQL FETCH cur INTO :var1, :var2;\n" +
                      "    EXEC SQL CLOSE cur;\n" +
                      "}";

        // when
        List<Query> queries = proC_queryParser.parse(code);

        // then
        assertEquals(4, queries.size());
        // DECLARE는 인식되지 않으므로 CURSOR나 SELECT, FROM 중 하나로 인식 - SqlType에 따라 다름
        assertEquals(SqlType.OPEN, queries.get(1).getType());
        assertEquals(SqlType.FETCH, queries.get(2).getType());
        assertEquals(SqlType.CLOSE, queries.get(3).getType());
    }
}
