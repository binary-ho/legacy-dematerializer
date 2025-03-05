package binary.ho.query.table;

public class QueryMainTableParser {

    public static final char PARENTHESIS = '(';
    public static final char LOWER_BAR = '_';

    public static Table parseMainTableAfterKeyword(String sql, String keyword) {
        if (!hasSqlTableKeyword(sql, keyword)) {
            return Table.createNotFoundTable();
        }

        int nextWordIndex = getNextIdentifierIndex(sql, keyword);
        if (isParenthesis(sql, nextWordIndex)) {
            return Table.createViewTable();
        }

        String tableName = readIdentifier(sql, nextWordIndex);
        return Table.create(tableName);
    }

    private static boolean hasSqlTableKeyword(String sql, String keyword) {
        return sql.contains(keyword);
    }

    private static int getNextIdentifierIndex(String sql, String keyword) {
        int keywordEndIndex = getKeywordEndIndex(sql, keyword);
        return getNextWordIndex(sql, keywordEndIndex);
    }

    private static int getKeywordEndIndex(String sql, String keyword) {
        int index = sql.indexOf(keyword);
        return index + keyword.length();
    }

    private static int getNextWordIndex(String text, int index) {
        while (isWhiteSpace(text, index)) {
            index++;
        }
        return index;
    }

    private static boolean isWhiteSpace(String text, int index) {
        return index < text.length() && Character.isWhitespace(text.charAt(index));
    }

    private static boolean isParenthesis(String sql, int nextWordIndex) {
        return nextWordIndex < sql.length() && sql.charAt(nextWordIndex) == PARENTHESIS;
    }

    private static String readIdentifier(String sql, int index) {
        StringBuilder stringBuilder = new StringBuilder();
        while (index < sql.length()) {
            char current = sql.charAt(index);
            if (isNotIdentifier(current)) {
                break;
            }
            stringBuilder.append(current);
            index++;
        }
        return stringBuilder.toString();
    }

    private static boolean isNotIdentifier(char current) {
        return !Character.isLetterOrDigit(current) && current != LOWER_BAR;
    }
}
