package binary.ho.util.function;

import binary.ho.model.Query;
import binary.ho.model.SqlType;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProC_QueryParser {

    private static final Pattern PRO_C_SQL_PATTERN = Pattern.compile(
        "EXEC\\s+SQL\\s+([^;]*);",
        Pattern.CASE_INSENSITIVE | Pattern.DOTALL
    );

    public List<Query> parse(String code) {
        Matcher matcher = PRO_C_SQL_PATTERN.matcher(code);

        List<Query> queries = new LinkedList<>();
        while (matcher.find()) {
            String query = matcher.group();
            SqlType type = SqlType.fromString(query);
            queries.add(new Query(type, query));
        }
        return queries;
    }

    public String removeSqlStatements(String code) {
        return PRO_C_SQL_PATTERN.matcher(code).replaceAll("");
    }
}
