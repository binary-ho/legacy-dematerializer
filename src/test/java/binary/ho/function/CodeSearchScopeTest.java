package binary.ho.function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CodeSearchScopeTest {

    @Test
    @DisplayName("생성 시 코드 내용과 초기 인덱스를 가진다")
    void initializeWithCode() {
        // given
        String code = "int a = 10;";

        // when
        CodeSearchScope codeSearchScope = new CodeSearchScope(code);

        // then
        assertEquals(code, codeSearchScope.getRemainingCode());
        assertEquals(0, codeSearchScope.getScopeStart());
        assertFalse(codeSearchScope.isOutOfScope());
    }

    @Test
    @DisplayName("인덱스를 이동시킬 수 있다.")
    void multipleAdvances() {
        // given
        String code = "int a = 10; int b = 20; int c = 30;";
        CodeSearchScope codeSearchScope = new CodeSearchScope(code);

        // when
        codeSearchScope.move(10);
        assertEquals("; int b = 20; int c = 30;", codeSearchScope.getRemainingCode());

        codeSearchScope.move(20);
        assertEquals("20; int c = 30;", codeSearchScope.getRemainingCode());

        codeSearchScope.move(30);
        assertEquals("= 30;", codeSearchScope.getRemainingCode());

        // then
        assertEquals(30, codeSearchScope.getScopeStart());
        assertFalse(codeSearchScope.isOutOfScope());
    }

    @Test
    @DisplayName("인덱스가 코드 길이를 초과하면 스코프를 벗어난다")
    void outOfScopeWhenIndexExceedsLength() {
        // given
        String code = "int a = 10;";
        CodeSearchScope codeSearchScope = new CodeSearchScope(code);

        // when
        codeSearchScope.move(code.length());

        // then
        assertEquals("", codeSearchScope.getRemainingCode());
        assertEquals(code.length(), codeSearchScope.getScopeStart());
        assertTrue(codeSearchScope.isOutOfScope());
    }

    @Test
    @DisplayName("인덱스가 코드 길이를 초과해도 오류가 발생하지 않는다")
    void noCrashWhenIndexExceedsLength() {
        // given
        String code = "int a = 10;";
        CodeSearchScope codeSearchScope = new CodeSearchScope(code);

        // when
        codeSearchScope.move(code.length() * 2);

        // then
        assertEquals("", codeSearchScope.getRemainingCode());
        assertEquals(code.length() * 2, codeSearchScope.getScopeStart());
        assertTrue(codeSearchScope.isOutOfScope());
    }
}
