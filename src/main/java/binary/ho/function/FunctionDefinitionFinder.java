package binary.ho.function;

import binary.ho.model.Query;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FunctionDefinitionFinder {

    private final Pattern functionDefinitionPattern;
    private final Set<String> excludeFunctions;
    private final FunctionCallFinder functionCallFinder;

    public FunctionDefinitionFinder(
        Pattern functionDefinitionPattern,
        Set<String> excludeFunctions, FunctionCallFinder functionCallFinder) {
        this.functionDefinitionPattern = functionDefinitionPattern;
        this.excludeFunctions = excludeFunctions;
        this.functionCallFinder = functionCallFinder;
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

        String functionName = matcher.group(1);
        if (excludeFunctions.contains(functionName)) {
            codeSearchScope.advance(matcher.end());
            return hasNext(codeSearchScope);
        }

        // 2. 함수 범위 구하기
        int startIndex = codeSearchScope.getScopeStart() + matcher.end() - 1;
        int endIndex = findMatchingBrace(targetCode, startIndex);
        if (endIndex == -1) {
            codeSearchScope.advance(matcher.end());
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

        int startIndex = codeSearchScope.getScopeStart() + matcher.end() - 1;
        int endIndex = findMatchingBrace(targetCode, startIndex);
        codeSearchScope.advance(endIndex + 1);

        String functionBody = targetCode.substring(startIndex + 1, endIndex);

        List<Query> queries = ProC_QueryParser.parse(functionBody);

        String removedQuery = ProC_QueryParser.removeSqlStatements(functionBody);
        String functionName = matcher.group(1);
        List<String> functionCalls = functionCallFinder.findAll(functionName, removedQuery);

        return new Function(functionName, removedQuery, queries, functionCalls);
    }

    private int findMatchingBrace(String text, int startPos) {
        int braceCount = 1;

        for (int i = startPos + 1; i < text.length(); i++) {
            char c = text.charAt(i);

            if (c == '{') {
                braceCount++;
            } else if (c == '}') {
                braceCount--;
                if (braceCount == 0) {
                    return i;
                }
            }
        }

        return -1;
    }
}
