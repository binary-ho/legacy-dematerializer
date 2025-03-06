package binary.ho.query;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum SqlType {

    SELECT, INSERT, UPDATE, DELETE,
    CREATE, ALTER, DROP, TRUNCATE,
    MERGE,
    OPEN, FETCH, CLOSE,
    COMMIT, ROLLBACK,
    EXCLUSIVE_TYPE,
    NOT_FOUND;  // default value

    private static final Pattern SQL_KEYWORD_PATTERN = Pattern.compile("\\b[A-Z]+\\b");
    // TODO: 설정파일로 분리
    private static final Set<SqlType> EXCLUSIVE_TYPES = Set.of(
        OPEN, FETCH, CLOSE, COMMIT, ROLLBACK
    );

    public static SqlType fromString(String query) {
        if (query == null || query.isEmpty()) {
            return NOT_FOUND;
        }

        String upperCaseQuery = query.toUpperCase();
        Matcher matcher = SQL_KEYWORD_PATTERN.matcher(upperCaseQuery);
        while (matcher.find()) {
            String keyword = matcher.group();
            if (isNotFound(keyword)) {
                continue;
            }
            SqlType sqlType = valueOf(keyword);
            if (EXCLUSIVE_TYPES.contains(sqlType)) {
                return EXCLUSIVE_TYPE;
            }
            return sqlType;
        }
        return NOT_FOUND;
    }

    private static boolean isNotFound(String keyword) {
        try {
            valueOf(keyword);
            return false;
        } catch (IllegalArgumentException e) {
            return true;
        }
    }
}