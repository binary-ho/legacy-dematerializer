package binary.ho.string;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StringCharBoundsTest {

    @Test
    @DisplayName("문자열 상태를 감지하고 업데이트한다")
    void detectAndUpdateStringState() {
        // given
        String code = "String s = \"Hello\";";
        StringCharBounds bounds = new StringCharBounds();

        // when - then: 문자열 시작 이전
        assertTrue(bounds.outOfString());

        // when - then: 큰따옴표 진입
        int quoteIndex = code.indexOf('"');
        bounds.update(code.charAt(quoteIndex));
        assertFalse(bounds.outOfString());

        // when - then: 문자열 내부 문자들
        for (int i = quoteIndex + 1; i < code.indexOf('"', quoteIndex + 1); i++) {
            bounds.update(code.charAt(i));
            assertFalse(bounds.outOfString());
        }

        // when - then: 문자열 종료 큰따옴표
        int endQuoteIndex = code.indexOf('"', quoteIndex + 1);
        bounds.update(code.charAt(endQuoteIndex));
        assertTrue(bounds.outOfString());
    }

    @Test
    @DisplayName("문자 리터럴 상태를 감지하고 업데이트한다")
    void detectAndUpdateCharacterState() {
        // given
        String code = "char c = 'A';";
        StringCharBounds bounds = new StringCharBounds();

        // when - then: 문자 시작 이전
        assertTrue(bounds.outOfString());

        // when - then: 작은따옴표 진입
        int quoteIndex = code.indexOf('\'');
        bounds.update(code.charAt(quoteIndex));
        assertFalse(bounds.outOfString());

        // when - then: 문자 내부
        bounds.update(code.charAt(quoteIndex + 1));
        assertFalse(bounds.outOfString());

        // when - then: 문자 종료 작은따옴표
        int endQuoteIndex = code.indexOf('\'', quoteIndex + 1);
        bounds.update(code.charAt(endQuoteIndex));
        assertTrue(bounds.outOfString());
    }

    @Test
    @DisplayName("이스케이프된 큰따옴표를 올바르게 처리한다")
    void handleEscapedDoubleQuotes() {
        // given
        String code = "String s = \"escaped \\\"quote\\\"\";";
        StringCharBounds bounds = new StringCharBounds();

        // when - then: 문자열 진입
        int startQuote = code.indexOf('"');
        bounds.update(code.charAt(startQuote));
        assertFalse(bounds.outOfString());

        // when - then: 첫 번째 백슬래시
        int backslashIndex = code.indexOf('\\');
        bounds.update(code.charAt(backslashIndex));
        assertFalse(bounds.outOfString());

        // when - then: 이스케이프된 큰따옴표는 문자열을 종료하지 않음
        bounds.update(code.charAt(backslashIndex + 1));
        assertFalse(bounds.outOfString());

        // when - then: 문자열 내용
        for (int i = backslashIndex + 2; i < code.lastIndexOf('"'); i++) {
            bounds.update(code.charAt(i));
            assertFalse(bounds.outOfString());
        }

        // when - then: 마지막 큰따옴표에서 문자열 종료
        bounds.update(code.charAt(code.lastIndexOf('"')));
        assertTrue(bounds.outOfString());
    }

    @Test
    @DisplayName("이스케이프된 작은따옴표를 올바르게 처리한다")
    void handleEscapedSingleQuotes() {
        // given
        String code = "char c = '\\'';";
        StringCharBounds bounds = new StringCharBounds();

        // when - then: 문자 진입
        int startQuote = code.indexOf('\'');
        bounds.update(code.charAt(startQuote));
        assertFalse(bounds.outOfString());

        // when - then: 백슬래시
        int backslashIndex = code.indexOf('\\');
        bounds.update(code.charAt(backslashIndex));
        assertFalse(bounds.outOfString());

        // when - then: 이스케이프된 작은따옴표는 문자를 종료하지 않음
        bounds.update(code.charAt(backslashIndex + 1));
        assertFalse(bounds.outOfString());

        // when - then: 마지막 작은따옴표에서 문자 종료
        bounds.update(code.charAt(code.lastIndexOf('\'')));
        assertTrue(bounds.outOfString());
    }

    @Test
    @DisplayName("다중 문자열 및 문자를 올바르게 처리한다")
    void handleMultipleStringsAndChars() {
        // given
        String code = "String s1 = \"First\"; char c = 'A'; String s2 = \"Second\";";
        StringCharBounds bounds = new StringCharBounds();

        // when - then: 첫 번째 문자열
        int firstStringStart = code.indexOf('"');
        bounds.update(code.charAt(firstStringStart));
        assertFalse(bounds.outOfString());

        int firstStringEnd = code.indexOf('"', firstStringStart + 1);
        for (int i = firstStringStart + 1; i <= firstStringEnd; i++) {
            bounds.update(code.charAt(i));
        }
        assertTrue(bounds.outOfString());

        // when - then: 문자 리터럴
        int charStart = code.indexOf('\'');
        bounds.update(code.charAt(charStart));
        assertFalse(bounds.outOfString());

        int charEnd = code.indexOf('\'', charStart + 1);
        for (int i = charStart + 1; i <= charEnd; i++) {
            bounds.update(code.charAt(i));
        }
        assertTrue(bounds.outOfString());

        // when - then: 두 번째 문자열
        int secondStringStart = code.lastIndexOf('"', code.length() - 3);
        bounds.update(code.charAt(secondStringStart));
        assertFalse(bounds.outOfString());

        int secondStringEnd = code.lastIndexOf('"');
        for (int i = secondStringStart + 1; i <= secondStringEnd; i++) {
            bounds.update(code.charAt(i));
        }
        assertTrue(bounds.outOfString());
    }

    @Test
    @DisplayName("따옴표가 아닌 문자를 확인한다")
    void checkIfCharIsNotQuote() {
        // given
        String code = "abc\"def'ghi";
        StringCharBounds bounds = new StringCharBounds();

        // when - then
        assertTrue(bounds.isNotQuote(code.charAt(0)));  // 'a'
        assertTrue(bounds.isNotQuote(code.charAt(1)));  // 'b'
        assertTrue(bounds.isNotQuote(code.charAt(2)));  // 'c'
        assertFalse(bounds.isNotQuote(code.charAt(3))); // '"'
        assertTrue(bounds.isNotQuote(code.charAt(4)));  // 'd'
        assertTrue(bounds.isNotQuote(code.charAt(5)));  // 'e'
        assertTrue(bounds.isNotQuote(code.charAt(6)));  // 'f'
        assertFalse(bounds.isNotQuote(code.charAt(7))); // '''
        assertTrue(bounds.isNotQuote(code.charAt(8)));  // 'g'
    }
}
