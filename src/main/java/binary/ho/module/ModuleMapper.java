package binary.ho.module;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ModuleMapper {

    private final Map<String, CModule> nameToModule;

    public ModuleMapper(List<CModule> modules) {
        this.nameToModule = modules.stream()
            .collect(Collectors.toMap(CModule::getModuleName, module -> module));
    }

    public CModule get(String moduleName) {
        return nameToModule.getOrDefault(
            moduleName, MissingModuleFactory.create()
        );
    }
}
