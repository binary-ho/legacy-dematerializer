package binary.ho.query;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import binary.ho.config.TestConfigManager;
import binary.ho.query.table.TableType;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
            "    EXEC SQL SELECT * FROM EMPLOYEES;\n" +
            "    process();\n" +
            "    EXEC SQL INSERT INTO LOGS VALUES (:id);\n" +
            "}";

        // when
        List<Query> queries = proC_queryParser.parse(code);

        // then
        assertEquals(2, queries.size());
        assertEquals(SqlType.SELECT, queries.get(0).getType());
        assertEquals(SqlType.INSERT, queries.get(1).getType());

        // 테이블 이름 확인
        assertEquals("EMPLOYEES", queries.get(0).getMainTable().getName());
        assertEquals("LOGS", queries.get(1).getMainTable().getName());
    }

    @Test
    @DisplayName("여러 줄에 걸친 Pro*C SQL 쿼리를 파싱할 수 있다")
    void parseMultilineSqlQueries() {
        // given
        String code = "void function() {\n" +
            "    EXEC SQL \n" +
            "        SELECT * \n" +
            "        FROM EMPLOYEES \n" +
            "        WHERE id = :id;\n" +
            "}";

        // when
        List<Query> queries = proC_queryParser.parse(code);

        // then
        assertEquals(1, queries.size());
        assertEquals(SqlType.SELECT, queries.get(0).getType());
        assertEquals("EMPLOYEES", queries.get(0).getMainTable().getName());
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
            "    EXEC SQL SELECT * FROM TABLE1;\n" +
            "    EXEC SQL INSERT INTO TABLE2 VALUES (:a, :b);\n" +
            "    EXEC SQL UPDATE TABLE3 SET col = :val;\n" +
            "    EXEC SQL DELETE FROM TABLE4;\n" +
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

        // 테이블 이름 확인
        assertEquals("TABLE1", queries.get(0).getMainTable().getName());
        assertEquals("TABLE2", queries.get(1).getMainTable().getName());
        assertEquals("TABLE3", queries.get(2).getMainTable().getName());
        assertEquals("TABLE4", queries.get(3).getMainTable().getName());
        assertTrue(
            queries.get(4).getMainTable().getName().equals(TableType.TABLE_NOT_FOUND.name()));
        assertTrue(
            queries.get(5).getMainTable().getName().equals(TableType.TABLE_NOT_FOUND.name()));
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

    @Test
    @DisplayName("서브쿼리가 있는 SELECT 문에서 메인 테이블을 올바르게 추출한다")
    void extractMainTableFromSelectWithSubquery() {
        // given
        String code = "void function() {\n" +
            "    EXEC SQL SELECT * FROM (SELECT id FROM INNER_TABLE) t, MAIN_TABLE WHERE t.id = MAIN_TABLE.id;\n"
            + "}";

        // when
        List<Query> queries = proC_queryParser.parse(code);

        // then
        assertEquals(1, queries.size());
        assertEquals(SqlType.SELECT, queries.get(0).getType());
        assertEquals(TableType.VIEW_TABLE.name(), queries.get(0).getMainTable().getName());
    }

    @Test
    @DisplayName("JOIN이 있는 SELECT 문에서 첫 번째 테이블을 올바르게 추출한다")
    void extractFirstTableFromSelectWithJoin() {
        // given
        String code = "void function() {\n" +
            "    EXEC SQL SELECT * FROM TABLE_A JOIN TABLE_B ON TABLE_A.id = TABLE_B.id;\n" +
            "}";

        // when
        List<Query> queries = proC_queryParser.parse(code);

        // then
        assertEquals(1, queries.size());
        assertEquals(SqlType.SELECT, queries.get(0).getType());
        assertEquals("TABLE_A", queries.get(0).getMainTable().getName());
    }

    @Test
    @DisplayName("복잡한 중첩 서브쿼리가 있는 SELECT 문에서 메인 테이블을 올바르게 추출한다")
    void extractMainTableFromSelectWithComplexNestedSubqueries() {
        // given
        String code = "void function() {\n" +
            "    EXEC SQL SELECT * FROM (SELECT * FROM (SELECT id FROM DEEP_TABLE) d, INNER_TABLE WHERE d.id = INNER_TABLE.id) s, MAIN_TABLE WHERE s.id = MAIN_TABLE.id;\n"
            +
            "}";

        // when
        List<Query> queries = proC_queryParser.parse(code);

        // then
        assertEquals(1, queries.size());
        assertEquals(SqlType.SELECT, queries.get(0).getType());
        assertEquals(TableType.VIEW_TABLE.name(), queries.get(0).getMainTable().getName());
    }

    @Test
    @DisplayName("UNION이 있는 SELECT 문에서 첫 번째 쿼리의 테이블을 올바르게 추출한다")
    void extractTableFromSelectWithUnion() {
        // given
        String code = "void function() {\n" +
            "    EXEC SQL SELECT * FROM TABLE_A UNION SELECT * FROM TABLE_B;\n" +
            "}";

        // when
        List<Query> queries = proC_queryParser.parse(code);

        // then
        assertEquals(1, queries.size());
        assertEquals(SqlType.SELECT, queries.get(0).getType());
        assertEquals("TABLE_A", queries.get(0).getMainTable().getName());
    }

    @Test
    @DisplayName("여러 테이블이 콤마로 구분된 SELECT 문에서 첫 번째 테이블을 올바르게 추출한다")
    void extractFirstTableFromSelectWithMultipleTables() {
        // given
        String code = "void function() {\n" +
            "    EXEC SQL SELECT * FROM TABLE_A, TABLE_B, TABLE_C WHERE TABLE_A.id = TABLE_B.id AND TABLE_B.id = TABLE_C.id;\n"
            +
            "}";

        // when
        List<Query> queries = proC_queryParser.parse(code);

        // then
        assertEquals(1, queries.size());
        assertEquals(SqlType.SELECT, queries.get(0).getType());
        assertEquals("TABLE_A", queries.get(0).getMainTable().getName());
    }

    @Test
    @DisplayName("INSERT 문에서 테이블 이름을 올바르게 추출한다")
    void extractTableFromInsert() {
        // given
        String code = "void function() {\n" +
            "    EXEC SQL INSERT INTO TARGET_TABLE (col1, col2) VALUES (:val1, :val2);\n" +
            "}";

        // when
        List<Query> queries = proC_queryParser.parse(code);

        // then
        assertEquals(1, queries.size());
        assertEquals(SqlType.INSERT, queries.get(0).getType());
        assertEquals("TARGET_TABLE", queries.get(0).getMainTable().getName());
    }

    @Test
    @DisplayName("UPDATE 문에서 테이블 이름을 올바르게 추출한다")
    void extractTableFromUpdate() {
        // given
        String code = "void function() {\n" +
            "    EXEC SQL UPDATE TARGET_TABLE SET col1 = :val1, col2 = :val2 WHERE id = :id;\n" +
            "}";

        // when
        List<Query> queries = proC_queryParser.parse(code);

        // then
        assertEquals(1, queries.size());
        assertEquals(SqlType.UPDATE, queries.get(0).getType());
        assertEquals("TARGET_TABLE", queries.get(0).getMainTable().getName());
    }

    @Test
    @DisplayName("DELETE 문에서 테이블 이름을 올바르게 추출한다")
    void extractTableFromDelete() {
        // given
        String code = "void function() {\n" +
            "    EXEC SQL DELETE FROM TARGET_TABLE WHERE id = :id;\n" +
            "}";

        // when
        List<Query> queries = proC_queryParser.parse(code);

        // then
        assertEquals(1, queries.size());
        assertEquals(SqlType.DELETE, queries.get(0).getType());
        assertEquals("TARGET_TABLE", queries.get(0).getMainTable().getName());
    }

    @Test
    @DisplayName("매우 복잡한 ProC SQL 구문에서 테이블 이름을 올바르게 추출한다")
    void extractTableFromComplexProCSql() {
        // given
        String code = "void function() {\n" +
            "    EXEC SQL\n" +
            "    SELECT r.*, m.description\n" +
            "    FROM RECURSIVE_CTE r\n" +
            "    LEFT JOIN METADATA_TABLE m ON r.id = m.id\n" +
            "    WHERE r.name LIKE :pattern\n" +
            "    ORDER BY r.id;\n" +
            "}";

        // when
        List<Query> queries = proC_queryParser.parse(code);

        // then
        assertEquals(1, queries.size());
        assertEquals(SqlType.SELECT, queries.get(0).getType());
        assertEquals("RECURSIVE_CTE", queries.get(0).getMainTable().getName());
    }
}
