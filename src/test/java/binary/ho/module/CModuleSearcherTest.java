package binary.ho.module;

import binary.ho.function.Function;
import binary.ho.function.FunctionParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class CModuleSearcherTest {

    @TempDir
    Path tempDir;
    
    @Mock
    private FunctionParser functionParser;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // FunctionParser 모의 객체 설정
        when(functionParser.parse(anyString())).thenReturn(
            List.of(Function.create("testFunction", List.of(), List.of()))
        );
    }
    
    @Test
    @DisplayName("유효한 디렉토리에서 C 및 PC 파일을 찾아 모듈로 변환한다")
    void searchFromValidDirectory() throws IOException {
        // given
        // C 파일 생성
        Path cFilePath = tempDir.resolve("module_file1.c");
        Files.writeString(cFilePath, "void testFunction() { }");
        
        // PC 파일 생성
        Path pcFilePath = tempDir.resolve("module_file2.pc");
        Files.writeString(pcFilePath, "void testFunction() { }");
        
        // when
        List<CModule> modules = CModuleSearcher.searchFrom(tempDir.toString(), functionParser);
        
        // then
        assertEquals(2, modules.size());
        assertTrue(modules.stream().anyMatch(m -> m.getModuleName().equals("file1")));
        assertTrue(modules.stream().anyMatch(m -> m.getModuleName().equals("file2")));
    }
    
    @Test
    @DisplayName("하위 디렉토리의 파일도 모두 검색한다")
    void searchNestedDirectories() throws IOException {
        // given
        // 루트 디렉토리에 파일 생성
        Path rootFile = tempDir.resolve("root_file.c");
        Files.writeString(rootFile, "void testFunction() { }");
        
        // 하위 디렉토리 생성 및 파일 생성
        Path subDir = tempDir.resolve("subdir");
        Files.createDirectory(subDir);
        Path subFile = subDir.resolve("sub_file.pc");
        Files.writeString(subFile, "void testFunction() { }");
        
        // 중첩된 하위 디렉토리 생성 및 파일 생성
        Path nestedDir = subDir.resolve("nesteddir");
        Files.createDirectory(nestedDir);
        Path nestedFile = nestedDir.resolve("nested_file.c");
        Files.writeString(nestedFile, "void testFunction() { }");
        
        // when
        List<CModule> modules = CModuleSearcher.searchFrom(tempDir.toString(), functionParser);
        
        // then
        assertEquals(3, modules.size());
        assertTrue(modules.stream().anyMatch(m -> m.getModuleName().equals("file")));
        assertTrue(modules.stream().anyMatch(m -> m.getModuleName().equals("file")));
        assertTrue(modules.stream().anyMatch(m -> m.getModuleName().equals("file")));
    }
    
    @Test
    @DisplayName("C나 PC 파일이 아닌 파일은 무시한다")
    void ignoreNonCOrPCFiles() throws IOException {
        // given
        // C 파일 생성
        Path cFilePath = tempDir.resolve("module_file.c");
        Files.writeString(cFilePath, "void testFunction() { }");
        
        // 다른 확장자의 파일들 생성
        Path txtFile = tempDir.resolve("text_file.txt");
        Files.writeString(txtFile, "This is a text file");
        
        Path javaFile = tempDir.resolve("java_file.java");
        Files.writeString(javaFile, "public class Test {}");
        
        // when
        List<CModule> modules = CModuleSearcher.searchFrom(tempDir.toString(), functionParser);
        
        // then
        assertEquals(1, modules.size());
        assertEquals("file", modules.get(0).getModuleName());
    }
    
    @Test
    @DisplayName("존재하지 않는 디렉토리를 입력하면 예외가 발생한다")
    void throwExceptionForNonExistentDirectory() {
        // given
        String nonExistentPath = tempDir.toString() + "/nonexistent";
        
        // when & then
        assertThrows(IllegalArgumentException.class, () -> {
            CModuleSearcher.searchFrom(nonExistentPath, functionParser);
        });
    }
    
    @Test
    @DisplayName("파일을 입력하면 예외가 발생한다")
    void throwExceptionForFile() throws IOException {
        // given
        Path filePath = tempDir.resolve("test.txt");
        Files.writeString(filePath, "test content");
        
        // when & then
        assertThrows(IllegalArgumentException.class, () -> {
            CModuleSearcher.searchFrom(filePath.toString(), functionParser);
        });
    }
}
