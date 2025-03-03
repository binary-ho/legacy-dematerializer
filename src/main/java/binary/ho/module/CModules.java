package binary.ho.module;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CModules implements Serializable {

    private static final long serialVersionUID = 1L;
    private final Map<String, CModule> nameToModule;

    public CModules(List<CModule> modules) {
        this.nameToModule = modules.stream()
            .collect(Collectors.toMap(CModule::getModuleName, module -> module));
    }

    public CModule get(String moduleName) {
        return nameToModule.getOrDefault(
            moduleName, MissingModuleFactory.create()
        );
    }
}
