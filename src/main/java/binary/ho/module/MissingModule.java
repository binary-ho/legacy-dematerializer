package binary.ho.module;

import binary.ho.function.Function;
import binary.ho.function.MissingFunction;
import java.util.List;

public class MissingModule {

    public static final String MISSING_MODULE = "MISSING";

    public static CModule create() {
        Function missingFunction = MissingFunction.create();
        return new CModule(MISSING_MODULE, List.of(missingFunction));
    }
}
