package binary.ho.query;

import binary.ho.query.table.QueryMainTableParser;
import binary.ho.query.table.Table;
import binary.ho.query.table.TableKeyword;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProC_QueryParser {

    private final Pattern proC_SqlPattern;

    public ProC_QueryParser(Pattern proC_SqlPattern) {
        this.proC_SqlPattern = proC_SqlPattern;
    }

    public List<Query> parse(String code) {
        Matcher matcher = proC_SqlPattern.matcher(code);

        List<Query> queries = new LinkedList<>();
        while (matcher.find()) {
            String proCQuery = matcher.group();
            Query query = parseQueryText(proCQuery);
            queries.add(query);
        }
        return queries;
    }

    private Query parseQueryText(String query) {
        SqlType type = SqlType.fromString(query);

        TableKeyword tableKeyword = TableKeyword.of(type);
        if (tableKeyword == TableKeyword.NOT_SUPPORTED) {
            return new Query(type, Table.createNotSupportedTable(), query);
        }

        String keyword = tableKeyword.getKeyword();
        Table table = QueryMainTableParser.parseMainTableAfterKeyword(query, keyword);
        return new Query(type, table, query);
    }
}
