package binary.ho.module;

import static org.junit.jupiter.api.Assertions.assertEquals;

import binary.ho.function.Function;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CModulesTest {

    @Test
    @DisplayName("모듈 이름으로 특정 모듈을 가져올 수 있다")
    void getModuleByName() {
        // given
        List<CModule> moduleList = new ArrayList<>();

        // 첫 번째 모듈
        String filePath1 = "/path/to/module_file1.pc";
        List<Function> functions1 = new ArrayList<>();
        functions1.add(Function.create("function1", List.of(), List.of()));
        CModule module1 = new CModule(filePath1, functions1);

        // 두 번째 모듈
        String filePath2 = "/path/to/module_file2.pc";
        List<Function> functions2 = new ArrayList<>();
        functions2.add(Function.create("function2", List.of(), List.of()));
        CModule module2 = new CModule(filePath2, functions2);

        moduleList.add(module1);
        moduleList.add(module2);

        // when
        CModules modules = new CModules(moduleList);

        // then
        assertEquals("module_file1", modules.get("module_file1").getModuleName());
        assertEquals("module_file2", modules.get("module_file2").getModuleName());
    }

    @Test
    @DisplayName("존재하지 않는 모듈을 요청하면 MISSING 모듈이 반환된다")
    void returnMissingModuleWhenNotFound() {
        // given
        List<CModule> moduleList = new ArrayList<>();

        String filePath = "/path/to/module_file.pc";
        List<Function> functions = new ArrayList<>();
        functions.add(Function.create("function1", List.of(), List.of()));
        CModule module = new CModule(filePath, functions);

        moduleList.add(module);

        // when
        CModules modules = new CModules(moduleList);

        // then
        assertEquals(MissingModuleFactory.MISSING_MODULE,
            modules.get("nonexistent").getModuleName());
    }
}
