package binary.ho.file;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class FileNameExtractorTest {

    @ParameterizedTest
    @CsvSource({
        "/path/to/module_file.pc, module_file",
        "C:/path/to/module_file.pc, module_file",
        "/path/to/first_second_third.pc, first_second_third",
        "/path/to/first.second.third.pc, first",
        "file_name_only.pc, file_name_only"
    })
    @DisplayName("파일 경로에서 파일 이름을 추출한다")
    void extractFileNameFromFilePath(String filePath, String expected) {
        // when
        String result = FileNameExtractor.extractFileName(filePath);

        // then
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("점이 여러 개 있는 파일 이름에서 마지막 부분을 올바르게 추출한다")
    void extractFileNameFromMultipleDots() {
        // given
        String filePath = "/path/to/module_file.name.pc";

        // when
        String result = FileNameExtractor.extractFileName(filePath);

        // then
        assertEquals("module_file", result);
    }

    @Test
    @DisplayName("윈도우 스타일 경로에서도 마지막 부분을 올바르게 추출한다")
    void extractFileNameFromWindowsStylePath() {
        // given
        String filePath = "C:\\path\\to\\module_file.pc";

        // when
        String result = FileNameExtractor.extractFileName(filePath);

        // then
        assertEquals("module_file", result);
    }
}
