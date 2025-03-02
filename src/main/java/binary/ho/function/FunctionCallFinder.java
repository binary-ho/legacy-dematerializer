package binary.ho.function;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FunctionCallFinder {

    private final Pattern functionCallPattern;
    private final Set<String> excludeFunctions;

    public FunctionCallFinder(Pattern functionCallPattern, Set<String> excludeFunctions) {
        this.functionCallPattern = functionCallPattern;
        this.excludeFunctions = excludeFunctions;
    }

    public List<String> findAll(String functionName, String functionBody) {
        List<String> functionCalls = new LinkedList<>();
        Matcher matcher = functionCallPattern.matcher(functionBody);
        while (matcher.find()) {
            String calledName = matcher.group(1);
            if (isNotTarget(functionName, calledName)) {
                continue;
            }
            functionCalls.add(calledName);
        }
        return functionCalls;
    }

    private boolean isNotTarget(String functionName, String calledName) {
        return excludeFunctions.contains(calledName) || calledName.equals(functionName);
    }
}
