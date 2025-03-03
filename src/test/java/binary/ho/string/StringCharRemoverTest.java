package binary.ho.string;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StringCharRemoverTest {

    @Test
    @DisplayName("문자열 리터럴을 제거한다")
    void removeStringLiterals() {
        // given
        String code = "String s = \"Hello, World!\";";

        // when
        String result = StringCharRemover.remove(code);

        // then
        String expected = "String s = ;";
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("문자 리터럴을 제거한다")
    void removeCharLiterals() {
        // given
        String code = "char c = 'A';";

        // when
        String result = StringCharRemover.remove(code);

        // then
        String expected = "char c = ;";
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("여러 문자열과 문자 리터럴을 제거한다")
    void removeMultipleStringsAndChars() {
        // given
        String code = "String s1 = \"First\"; char c = 'A'; String s2 = \"Second\";";

        // when
        String result = StringCharRemover.remove(code);

        // then
        String expected = "String s1 = ; char c = ; String s2 = ;";
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("이스케이프된 따옴표를 포함한 문자열을 올바르게 처리한다")
    void handleEscapedQuotesInStrings() {
        // given
        String code = "String s = \"This contains \\\"escaped quotes\\\"\";";

        // when
        String result = StringCharRemover.remove(code);

        // then
        String expected = "String s = ;";
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("이스케이프된 따옴표를 포함한 문자를 올바르게 처리한다")
    void handleEscapedQuotesInChars() {
        // given
        String code = "char c = '\\'';";

        // when
        String result = StringCharRemover.remove(code);

        // then
        String expected = "char c = ;";
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("여러 줄에 걸친 문자열을 올바르게 처리한다")
    void handleMultiLineStrings() {
        // given
        String code = "String multiLine = \"This is a\n" +
            "multi-line\n" +
            "string\";";

        // when
        String result = StringCharRemover.remove(code);

        // then
        String expected = "String multiLine = ;";
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("다양한 이스케이프 시퀀스를 포함한 문자열을 처리한다")
    void handleVariousEscapeSequences() {
        // given
        String code = "String s = \"Line1\\nLine2\\tTabbed\\\\Backslash\";";

        // when
        String result = StringCharRemover.remove(code);

        // then
        String expected = "String s = ;";
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("문자열이나 문자가 없는 코드는 그대로 반환한다")
    void returnUnchangedCodeWithoutStringsOrChars() {
        // given
        String code = "int a = 10; int b = 20; int sum = a + b;";

        // when
        String result = StringCharRemover.remove(code);

        // then
        assertEquals(code, result);
    }

    @Test
    @DisplayName("빈 문자열을 처리할 수 있다")
    void handleEmptyString() {
        // given
        String code = "";

        // when
        String result = StringCharRemover.remove(code);

        // then
        assertEquals("empty", result);
    }
}
