package binary.ho.function;

import binary.ho.query.ProC_QueryParser;
import binary.ho.query.ProC_QueryRemover;
import binary.ho.query.Query;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FunctionDefinitionFinder {

    private final Pattern functionDefinitionPattern;
    private final Set<String> excludeFunctions;
    private final FunctionCallFinder functionCallFinder;
    private final ProC_QueryParser proC_queryParser;
    private final ProC_QueryRemover proC_queryRemover;

    public FunctionDefinitionFinder(
        Pattern functionDefinitionPattern,
        Set<String> excludeFunctions,
        FunctionCallFinder functionCallFinder,
        ProC_QueryParser proC_QueryParser,
        ProC_QueryRemover proC_QueryRemover) {
        this.functionDefinitionPattern = functionDefinitionPattern;
        this.excludeFunctions = excludeFunctions;
        this.functionCallFinder = functionCallFinder;
        this.proC_queryParser = proC_QueryParser;
        this.proC_queryRemover = proC_QueryRemover;
    }

    public boolean hasNext(CodeSearchScope codeSearchScope) {
        if (codeSearchScope.isOutOfScope()) {
            return false;
        }

        String targetCode = codeSearchScope.getRemainingCode();
        Matcher matcher = functionDefinitionPattern.matcher(targetCode);
        if (!matcher.find()) {
            return false;
        }

        int startIndex = matcher.end() - 1;
        int endIndex = findMatchingBrace(targetCode, startIndex);
        if (endIndex == -1) {
            throw new IllegalStateException("No matching brace found. wrong file");
        }

        String functionName = matcher.group(1);
        if (excludeFunctions.contains(functionName)) {
            codeSearchScope.move(codeSearchScope.getScopeStart() + endIndex);
            return hasNext(codeSearchScope);
        }
        return true;
    }

    public Function next(CodeSearchScope codeSearchScope) {
        if (!hasNext(codeSearchScope)) {
            throw new IllegalStateException("No more function definition");
        }

        String targetCode = codeSearchScope.getRemainingCode();
        Matcher matcher = functionDefinitionPattern.matcher(targetCode);
        if (!matcher.find()) {
            throw new IllegalStateException("No function definition found");
        }

        int startIndex = matcher.end() - 1;
        int endIndex = findMatchingBrace(targetCode, startIndex);
        codeSearchScope.move(codeSearchScope.getScopeStart() + endIndex + 1);

        String functionBody = targetCode.substring(startIndex + 1, endIndex);

        List<Query> queries = proC_queryParser.parse(functionBody);
        String removedQuery = proC_queryRemover.removeSqlStatements(functionBody);
        String functionName = matcher.group(1);
        List<String> functionCalls = functionCallFinder.findAll(functionName, removedQuery);

        return Function.create(functionName, queries, functionCalls);
    }

    private int findMatchingBrace(String text, int startPos) {
        System.out.println("==Text Start==");
        System.out.println(text);
        System.out.println("==Text End==");
        int braceCount = 1;
        for (int i = startPos + 1; i < text.length(); i++) {
            char c = text.charAt(i);
            System.out.print(c);
            if (c == '{') {
                braceCount++;
            } else if (c == '}') {
                braceCount--;
                if (braceCount == 0) {
                    System.out.println("=======found=======");
                    return i;
                }
            }
        }

        System.out.println("=======notfound=======");
        return -1;
    }
}
