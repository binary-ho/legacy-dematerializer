package binary.ho.config;

import java.util.Properties;
import java.util.regex.Pattern;

public class PatternParser {

    private final FlagParser flagParser = new FlagParser();
    private static final String FLAG_POSTFIX = ".flags";

    public Pattern parse(Properties properties, String key) {
        String pattern = properties.getProperty(key);
        String flagsStr = properties.getProperty(key + FLAG_POSTFIX);

        int flags = flagParser.parse(flagsStr);
        return Pattern.compile(pattern, flags);
    }
}
