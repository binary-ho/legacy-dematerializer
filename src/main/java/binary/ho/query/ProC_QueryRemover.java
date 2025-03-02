package binary.ho.query;

import java.util.regex.Pattern;

public class ProC_QueryRemover {

    private final Pattern proC_SqlPattern;

    public ProC_QueryRemover(Pattern proC_SqlPattern) {
        this.proC_SqlPattern = proC_SqlPattern;
    }

    public String removeSqlStatements(String code) {
        return proC_SqlPattern.matcher(code)
            .replaceAll("");
    }
}
