package binary.ho.config;

import java.util.Collections;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;

public class TestConfigManager {

    private static final String FUNCTION_DEFINITION_CONFIG = "/function_definition.properties";
    private static final String FUNCTION_CALL_CONFIG = "/function_call.properties";
    private static final String PROC_SQL_CONFIG = "/proc_sql.properties";
    private static final String EXCLUDE_FUNCTIONS_CONFIG = "/exclude_functions.properties";

    private static final String FUNCTION_DEFINITION_PATTERN_KEY = "function.definition.pattern";
    private static final String FUNCTION_CALL_PATTERN_KEY = "function.call.pattern";
    private static final String PROC_SQL_PATTERN_KEY = "proc.sql.pattern";
    private static final String EXCLUDE_FUNCTIONS_KEY = "exclude.functions";

    private final Pattern functionDefinitionPattern;
    private final Pattern functionCallPattern;
    private final Pattern procSqlPattern;
    private final Set<String> excludeFunctions;

    private final PropertyLoader propertyLoader = new PropertyLoader();
    private final PatternParser patternParser = new PatternParser();

    public TestConfigManager() {
        this.functionDefinitionPattern = loadFunctionDefinitionPattern();
        this.functionCallPattern = loadFunctionCallPattern();
        this.procSqlPattern = loadProcSqlPattern();
        this.excludeFunctions = loadExcludeFunctions();
    }

    private Pattern loadFunctionDefinitionPattern() {
        Properties properties = propertyLoader.load(FUNCTION_DEFINITION_CONFIG);
        return patternParser.parse(properties, FUNCTION_DEFINITION_PATTERN_KEY);
    }

    private Pattern loadFunctionCallPattern() {
        Properties properties = propertyLoader.load(FUNCTION_CALL_CONFIG);
        return patternParser.parse(properties, FUNCTION_CALL_PATTERN_KEY);
    }

    private Pattern loadProcSqlPattern() {
        Properties properties = propertyLoader.load(PROC_SQL_CONFIG);
        return patternParser.parse(properties, PROC_SQL_PATTERN_KEY);
    }

    private Set<String> loadExcludeFunctions() {
        Properties properties = propertyLoader.load(EXCLUDE_FUNCTIONS_CONFIG);
        String functions = properties.getProperty(EXCLUDE_FUNCTIONS_KEY);
        if (functions == null || functions.trim().isEmpty()) {
            return Collections.emptySet();
        }
        return Set.of(functions.split(";"));
    }

    public Pattern getFunctionDefinitionPattern() {
        return functionDefinitionPattern;
    }

    public Pattern getFunctionCallPattern() {
        return functionCallPattern;
    }

    public Pattern getProcSqlPattern() {
        return procSqlPattern;
    }

    public Set<String> getExcludeFunctions() {
        return excludeFunctions;
    }
}
