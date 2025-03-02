package binary.ho.module;

import binary.ho.function.Function;
import binary.ho.function.MissingFunctionFactory;
import java.util.List;

public class MissingModuleFactory {

    public static final String MISSING_MODULE = "MISSING";

    public static CModule create() {
        Function missingFunction = MissingFunctionFactory.create();
        return new CModule(MISSING_MODULE, List.of(missingFunction));
    }
}
