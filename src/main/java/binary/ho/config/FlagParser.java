package binary.ho.config;

import java.util.regex.Pattern;

public class FlagParser {

    private static final String DOTALL = "DOTALL";
    private static final int NO_FLAG = 0;

    public int parse(String flag) {
        if (flag != null && flag.trim().equals(DOTALL)) {
            return Pattern.DOTALL;
        }
        return NO_FLAG;
    }
}
