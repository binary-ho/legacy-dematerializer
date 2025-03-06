package binary.ho.query;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class QueryCommentRemoverTest {

    @Test
    @DisplayName("SQL 쿼리에서 라인 주석을 제거한다")
    void removeLineComments() {
        // given
        String sql = "SELECT * FROM EMPLOYEES -- 직원 테이블에서 조회\nWHERE ID = 100";
        
        // when
        String result = QueryCommentRemover.removeComments(sql);
        
        // then
        assertEquals("SELECT * FROM EMPLOYEES \nWHERE ID = 100", result);
    }
    
    @Test
    @DisplayName("SQL 쿼리에서 여러 라인 주석을 제거한다")
    void removeMultipleLineComments() {
        // given
        String sql = "SELECT * -- 모든 컬럼 선택\n" +
                     "FROM EMPLOYEES -- 직원 테이블\n" +
                     "WHERE ID = 100 -- ID가 100인 직원";
        
        // when
        String result = QueryCommentRemover.removeComments(sql);
        
        // then
        assertEquals("SELECT * \n" +
                     "FROM EMPLOYEES \n" +
                     "WHERE ID = 100 ", result);
    }
    
    @Test
    @DisplayName("문자열 내부의 주석 기호는 제거하지 않는다")
    void doNotRemoveCommentsInsideStrings() {
        // given
        String sql = "SELECT * FROM EMPLOYEES WHERE NAME = '-- 이것은 주석이 아님'";
        
        // when
        String result = QueryCommentRemover.removeComments(sql);
        
        // then
        assertEquals("SELECT * FROM EMPLOYEES WHERE NAME = '-- 이것은 주석이 아님'", result);
    }
    
    @Test
    @DisplayName("문자 리터럴 내부의 주석 기호는 제거하지 않는다")
    void doNotRemoveCommentsInsideCharLiterals() {
        // given
        String sql = "SELECT * FROM EMPLOYEES WHERE CODE = '-'";
        
        // when
        String result = QueryCommentRemover.removeComments(sql);
        
        // then
        assertEquals("SELECT * FROM EMPLOYEES WHERE CODE = '-'", result);
    }
    
    @Test
    @DisplayName("이스케이프된 문자열 내부의 주석 기호는 제거하지 않는다")
    void doNotRemoveCommentsInsideEscapedStrings() {
        // given
        String sql = "SELECT * FROM EMPLOYEES WHERE TEXT = \"\\\"-- 이것은 주석이 아님\\\"\"";
        
        // when
        String result = QueryCommentRemover.removeComments(sql);
        
        // then
        assertEquals("SELECT * FROM EMPLOYEES WHERE TEXT = \"\\\"-- 이것은 주석이 아님\\\"\"", result);
    }
    
    @Test
    @DisplayName("주석이 없는 SQL 쿼리는 그대로 반환한다")
    void returnOriginalQueryWhenNoComments() {
        // given
        String sql = "SELECT * FROM EMPLOYEES WHERE ID = 100";
        
        // when
        String result = QueryCommentRemover.removeComments(sql);
        
        // then
        assertEquals(sql, result);
    }
    
    @Test
    @DisplayName("주석으로만 구성된 SQL 쿼리는 빈 문자열과 줄바꿈만 남긴다")
    void returnEmptyStringForQueryWithOnlyComments() {
        // given
        String sql = "-- 이것은 주석입니다\n-- 이것도 주석입니다";
        
        // when
        String result = QueryCommentRemover.removeComments(sql);
        
        // then
        assertEquals("\n", result);
    }
    
    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("null이나 빈 문자열이 입력되면 그대로 반환한다")
    void returnOriginalForNullOrEmptyInput(String input) {
        // when
        String result = QueryCommentRemover.removeComments(input);
        
        // then
        assertEquals(input, result);
    }
    
    @Test
    @DisplayName("주석 시작 기호 '--' 뒤에 문자열이 없는 경우도 처리한다")
    void handleCommentAtEndOfLine() {
        // given
        String sql = "SELECT * FROM EMPLOYEES --";
        
        // when
        String result = QueryCommentRemover.removeComments(sql);
        
        // then
        assertEquals("SELECT * FROM EMPLOYEES ", result);
    }
    
    @Test
    @DisplayName("연속된 대시 문자 중 두 개만 주석으로 인식한다")
    void recognizeOnlyTwoDashesAsComment() {
        // given
        String sql = "SELECT * FROM EMPLOYEES ---- 이중 주석";
        
        // when
        String result = QueryCommentRemover.removeComments(sql);
        
        // then
        assertEquals("SELECT * FROM EMPLOYEES ", result);
    }
}
