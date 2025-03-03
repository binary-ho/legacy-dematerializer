package binary.ho.file;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FileReaderTest {

    @TempDir
    Path tempDir;
    
    @Test
    @DisplayName("파일 내용을 읽어올 수 있다")
    void readFileContent() throws IOException {
        // given
        String expectedContent = "Sample file content\nwith multiple lines\nfor testing purposes";
        Path tempFile = tempDir.resolve("test-file.txt");
        Files.writeString(tempFile, expectedContent);
        
        // when
        String content = FileReader.readFileContent(tempFile.toFile());
        
        // then
        assertEquals(expectedContent, content);
    }
    
    @Test
    @DisplayName("빈 파일을 읽을 수 있다")
    void readEmptyFile() throws IOException {
        // given
        String expectedContent = "";
        Path tempFile = tempDir.resolve("empty-file.txt");
        Files.writeString(tempFile, expectedContent);
        
        // when
        String content = FileReader.readFileContent(tempFile.toFile());
        
        // then
        assertEquals(expectedContent, content);
    }
    
    @Test
    @DisplayName("존재하지 않는 파일을 읽으려 하면 예외가 발생한다")
    void throwExceptionWhenFileDoesNotExist() {
        // given
        File nonExistentFile = new File(tempDir.toFile(), "non-existent-file.txt");
        
        // when & then
        assertThrows(RuntimeException.class, () -> {
            FileReader.readFileContent(nonExistentFile);
        });
    }
    
    @Test
    @DisplayName("접근 권한이 없는 파일을 읽으려 하면 예외가 발생한다")
    void throwExceptionWhenFileIsNotReadable() throws IOException {
        // given
        Path tempFile = tempDir.resolve("non-readable-file.txt");
        Files.writeString(tempFile, "Some content");
        File file = tempFile.toFile();
        
        // 파일을 읽기 전용으로 만들고 실패하면 테스트를 건너뛰기
        if (!file.setReadable(false)) {
            System.out.println("파일 권한 변경이 불가능하여 테스트를 건너뜁니다.");
            return;
        }
        
        try {
            // when & then
            assertThrows(RuntimeException.class, () -> {
                FileReader.readFileContent(file);
            });
        } finally {
            // 테스트 후 파일 권한 복원
            file.setReadable(true);
        }
    }
    
    @Test
    @DisplayName("한글 등 유니코드 문자가 포함된 파일을 읽을 수 있다")
    void readFileWithUnicodeCharacters() throws IOException {
        // given
        String expectedContent = "유니코드 문자열 테스트\n한글, 日本語, Español, Русский";
        Path tempFile = tempDir.resolve("unicode-file.txt");
        Files.writeString(tempFile, expectedContent);
        
        // when
        String content = FileReader.readFileContent(tempFile.toFile());
        
        // then
        assertEquals(expectedContent, content);
    }
    
    @Test
    @DisplayName("대용량 파일을 읽을 수 있다")
    void readLargeFile() throws IOException {
        // given
        StringBuilder contentBuilder = new StringBuilder();
        for (int i = 0; i < 10000; i++) {
            contentBuilder.append("Line ").append(i).append(" of test content\n");
        }
        String expectedContent = contentBuilder.toString();
        Path tempFile = tempDir.resolve("large-file.txt");
        Files.writeString(tempFile, expectedContent);
        
        // when
        String content = FileReader.readFileContent(tempFile.toFile());
        
        // then
        assertEquals(expectedContent, content);
    }
}
