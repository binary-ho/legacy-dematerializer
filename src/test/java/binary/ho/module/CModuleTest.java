package binary.ho.module;

import binary.ho.function.Function;
import binary.ho.function.MissingFunctionFactory;
import binary.ho.query.Query;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CModuleTest {

    @Test
    @DisplayName("모듈 이름을 파일 경로에서 추출한다")
    void extractModuleNameFromFilePath() {
        // given
        String filePath = "/path/to/module_file.pc";
        List<Function> functions = new ArrayList<>();
        functions.add(Function.create("function1", List.of(), List.of()));
        
        // when
        CModule module = new CModule(filePath, functions);
        
        // then
        assertEquals("file", module.getModuleName());
    }
    
    @Test
    @DisplayName("대표 함수가 모듈 이름과 일치하는 함수로 선택된다")
    void selectRepresentativeFunctionMatchingModuleName() {
        // given
        String filePath = "/path/to/module_file.pc";
        List<Function> functions = new ArrayList<>();
        functions.add(Function.create("function1", List.of(), List.of()));
        functions.add(Function.create("file", List.of(), List.of()));
        functions.add(Function.create("function2", List.of(), List.of()));
        
        // when
        CModule module = new CModule(filePath, functions);
        
        // then
        assertEquals("file", module.getRepresentativeFunction().getName());
    }
    
    @Test
    @DisplayName("모듈 이름과 일치하는 함수가 없으면 첫 번째 함수가 대표 함수가 된다")
    void selectFirstFunctionAsRepresentativeWhenNoMatch() {
        // given
        String filePath = "/path/to/module_file.pc";
        List<Function> functions = new ArrayList<>();
        functions.add(Function.create("function1", List.of(), List.of()));
        functions.add(Function.create("function2", List.of(), List.of()));
        
        // when
        CModule module = new CModule(filePath, functions);
        
        // then
        assertEquals("function1", module.getRepresentativeFunction().getName());
    }
    
    @Test
    @DisplayName("함수가 없는 경우 MISSING 함수가 반환된다")
    void returnMissingFunctionWhenNoFunctions() {
        // given
        String filePath = "/path/to/module_file.pc";
        List<Function> emptyFunctions = new ArrayList<>();
        
        // when
        CModule module = new CModule(filePath, emptyFunctions);
        
        // then
        assertEquals(MissingFunctionFactory.create().getName(), module.getRepresentativeFunction().getName());
    }
    
    @Test
    @DisplayName("함수 이름으로 특정 함수를 가져올 수 있다")
    void getFunctionByName() {
        // given
        String filePath = "/path/to/module_file.pc";
        List<Function> functions = new ArrayList<>();
        functions.add(Function.create("function1", List.of(), List.of()));
        functions.add(Function.create("function2", List.of(), List.of()));
        
        // when
        CModule module = new CModule(filePath, functions);
        
        // then
        assertEquals("function1", module.getFunction("function1").getName());
        assertEquals("function2", module.getFunction("function2").getName());
    }
    
    @Test
    @DisplayName("외부 함수 호출 여부를 확인할 수 있다")
    void checkIfFunctionIsExternalCall() {
        // given
        String filePath = "/path/to/module_file.pc";
        List<Function> functions = new ArrayList<>();
        functions.add(Function.create("function1", List.of(), List.of()));
        functions.add(Function.create("function2", List.of(), List.of()));
        
        // when
        CModule module = new CModule(filePath, functions);
        
        // then
        assertFalse(module.isExternalCall("function1"));
        assertFalse(module.isExternalCall("function2"));
        assertTrue(module.isExternalCall("externalFunction"));
    }
}
