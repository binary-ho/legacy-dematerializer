package binary.ho.module;

import binary.ho.function.Function;
import binary.ho.function.MissingFunctionFactory;
import java.util.List;

public class MissingModuleFactory {

    public static final String MISSING_MODULE = "(MISSING) ";

    private MissingModuleFactory() {
    }

    public static CModule create(String moduleName) {
        Function missingFunction = MissingFunctionFactory.create(moduleName);
        return new CModule(MISSING_MODULE + moduleName, List.of(missingFunction));
    }
}
