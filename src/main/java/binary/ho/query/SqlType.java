package binary.ho.query;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum SqlType {

    SELECT, INSERT, UPDATE, DELETE,
    OPEN, FETCH, CLOSE,
    COMMIT, ROLLBACK,
    CREATE, ALTER, DROP, TRUNCATE,
    MERGE,
    QUERY;  // default value

    private static final Pattern SQL_KEYWORD_PATTERN = Pattern.compile("\\b[A-Z]+\\b");

    private static final Map<String, SqlType> KEYWORD_TO_TYPE_MAP = new HashMap<>();

    static {
        for (SqlType type : values()) {
            if (type != QUERY) {
                KEYWORD_TO_TYPE_MAP.put(type.name(), type);
            }
        }
    }

    public static SqlType fromString(String query) {
        if (query == null || query.isEmpty()) {
            return QUERY;
        }

        Matcher matcher = SQL_KEYWORD_PATTERN.matcher(query);
        while (matcher.find()) {
            String keyword = matcher.group();
            if (KEYWORD_TO_TYPE_MAP.containsKey(keyword)) {
                return KEYWORD_TO_TYPE_MAP.get(keyword);
            }
        }
        return QUERY;
    }
}