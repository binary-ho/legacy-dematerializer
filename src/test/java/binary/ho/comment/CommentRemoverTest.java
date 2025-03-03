package binary.ho.comment;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class CommentRemoverTest {
    
    @Test
    @DisplayName("블록 주석을 제거한다")
    void removeBlockComments() {
        // given
        String code = "int main() {\n" +
                      "    /* This is a block comment */\n" +
                      "    int a = 10;\n" +
                      "    return 0;\n" +
                      "}";
        
        // when
        String result = CommentRemover.removeComments(code);
        
        // then
        String expected = "int main() {\n" +
                          "    \n" +
                          "    int a = 10;\n" +
                          "    return 0;\n" +
                          "}";
        assertEquals(expected, result);
    }
    
    @Test
    @DisplayName("라인 주석을 제거한다")
    void removeLineComments() {
        // given
        String code = "int main() {\n" +
                      "    int a = 10; // This is a line comment\n" +
                      "    return 0;\n" +
                      "}";
        
        // when
        String result = CommentRemover.removeComments(code);
        
        // then
        String expected = "int main() {\n" +
                          "    int a = 10; \n" +
                          "    return 0;\n" +
                          "}";
        assertEquals(expected, result);
    }
    
    @Test
    @DisplayName("여러 줄에 걸친 블록 주석을 제거한다")
    void removeMultilineBlockComments() {
        // given
        String code = "int main() {\n" +
                      "    /* This is a\n" +
                      "       multi-line\n" +
                      "       block comment */\n" +
                      "    return 0;\n" +
                      "}";
        
        // when
        String result = CommentRemover.removeComments(code);
        
        // then
        String expected = "int main() {\n" +
                          "    \n" +
                          "    return 0;\n" +
                          "}";
        assertEquals(expected, result);
    }
    
    @Test
    @DisplayName("여러 개의 주석을 모두 제거한다")
    void removeMultipleComments() {
        // given
        String code = "int main() { // Start of main\n" +
                      "    /* Block comment 1 */\n" +
                      "    int a = 10; // Line comment\n" +
                      "    /* Block comment 2 */\n" +
                      "    return 0; // End of main\n" +
                      "}";
        
        // when
        String result = CommentRemover.removeComments(code);
        
        // then
        String expected = "int main() { \n" +
                          "    \n" +
                          "    int a = 10; \n" +
                          "    \n" +
                          "    return 0; \n" +
                          "}";
        assertEquals(expected, result);
    }
    
    @Test
    @DisplayName("주석이 없는 코드는 그대로 반환한다")
    void returnUnchangedCodeWithoutComments() {
        // given
        String code = "int main() {\n" +
                      "    int a = 10;\n" +
                      "    return 0;\n" +
                      "}";
        
        // when
        String result = CommentRemover.removeComments(code);
        
        // then
        assertEquals(code, result);
    }
    
    @Test
    @DisplayName("중첩된 주석 형태를 올바르게 처리한다")
    void handleNestedCommentFormats() {
        // given
        String code = "int main() {\n" +
                      "    /* Outer comment /* Nested appearance */ Not a comment */\n" +
                      "    int a = 10;\n" +
                      "    // Line comment /* Not a block comment */\n" +
                      "    return 0;\n" +
                      "}";
        
        // when
        String result = CommentRemover.removeComments(code);
        
        // then
        String expected = "int main() {\n" +
                          "     Not a comment */\n" +
                          "    int a = 10;\n" +
                          "    \n" +
                          "    return 0;\n" +
                          "}";
        assertEquals(expected, result);
    }
    
    @Test
    @DisplayName("슬래시 문자가 다른 용도로 사용된 코드를 올바르게 처리한다")
    void handleSlashesUsedInOtherContexts() {
        // given
        String code = "int divisionResult = 10 / 5; // Division\n" +
                      "String regex = \"/\\\\*/\"; /* Regex pattern */";
        
        // when
        String result = CommentRemover.removeComments(code);
        
        // then
        String expected = "int divisionResult = 10 / 5; \n" +
                          "String regex = \"/\\\\*/\"; ";
        assertEquals(expected, result);
    }
}
