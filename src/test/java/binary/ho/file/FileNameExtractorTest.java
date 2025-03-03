package binary.ho.file;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class FileNameExtractorTest {

    @ParameterizedTest
    @CsvSource({
        "/path/to/module_file.pc, file",
        "C:/path/to/module_file.pc, file",
        "/path/to/first_second_third.pc, third",
        "/path/to/prefix_suffix.c, suffix",
        "/no_directory_file.pc, file",
        "/single_file_name.pc, name",
        "file_name_only.pc, only"
    })
    @DisplayName("파일 경로에서 마지막 부분을 추출한다")
    void extractLastPartFromFilePath(String filePath, String expected) {
        // when
        String result = FileNameExtractor.extractLastPart(filePath);
        
        // then
        assertEquals(expected, result);
    }
    
    @Test
    @DisplayName("경로 구분자가 없는 파일 이름에서도 마지막 부분을 추출한다")
    void extractLastPartFromFileNameWithoutPath() {
        // given
        String fileName = "prefix_module_file.pc";
        
        // when
        String result = FileNameExtractor.extractLastPart(fileName);
        
        // then
        assertEquals("file", result);
    }
    
    @Test
    @DisplayName(".pc 확장자를 제거한다")
    void removePcExtension() {
        // given
        String filePath = "/path/to/module_file.pc";
        
        // when
        String result = FileNameExtractor.extractLastPart(filePath);
        
        // then
        assertEquals("file", result);
    }
    
    @Test
    @DisplayName(".c 확장자를 제거한다")
    void removeCExtension() {
        // given
        String filePath = "/path/to/module_file.c";
        
        // when
        String result = FileNameExtractor.extractLastPart(filePath);
        
        // then
        assertEquals("file", result);
    }
    
    @Test
    @DisplayName("구분자가 없는 파일 이름의 경우 전체 이름을 반환한다")
    void returnWholeNameWhenNoDelimiter() {
        // given
        String filePath = "/path/to/filename.pc";
        
        // when
        String result = FileNameExtractor.extractLastPart(filePath);
        
        // then
        assertEquals("filename", result);
    }
    
    @Test
    @DisplayName("다중 언더스코어가 있는 파일 이름에서 마지막 부분을 추출한다")
    void extractLastPartFromMultipleUnderscores() {
        // given
        String filePath = "/path/to/first_second_third_fourth.pc";
        
        // when
        String result = FileNameExtractor.extractLastPart(filePath);
        
        // then
        assertEquals("fourth", result);
    }
    
    @Test
    @DisplayName("점이 여러 개 있는 파일 이름에서 마지막 부분을 올바르게 추출한다")
    void extractLastPartFromMultipleDots() {
        // given
        String filePath = "/path/to/module_file.name.pc";
        
        // when
        String result = FileNameExtractor.extractLastPart(filePath);
        
        // then
        assertEquals("file", result);
    }
    
    @Test
    @DisplayName("윈도우 스타일 경로에서도 마지막 부분을 올바르게 추출한다")
    void extractLastPartFromWindowsStylePath() {
        // given
        String filePath = "C:\\path\\to\\module_file.pc";
        
        // when
        String result = FileNameExtractor.extractLastPart(filePath);
        
        // then
        assertEquals("file", result);
    }
}
