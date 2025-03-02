package binary.ho.function;

import java.util.List;

public class MissingFunction {

    private static final String MISSING_FUNCTION = "MISSING";

    public static Function create() {
        return new Function(MISSING_FUNCTION, "", List.of(), List.of());
    }
}
