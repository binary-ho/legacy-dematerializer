package binary.ho.function;

import java.util.List;

public class MissingFunctionFactory {

    private static final String MISSING_FUNCTION = "MISSING";

    private MissingFunctionFactory() {
    }

    public static Function create() {
        return Function.create(MISSING_FUNCTION, List.of(), List.of());
    }
}
