package binary.ho.function;

import binary.ho.comment.CommentRemover;
import binary.ho.string.StringCharRemover;
import java.util.LinkedList;
import java.util.List;

public class FunctionParser {

    private final FunctionDefinitionFinder functionDefinitionFinder;

    public FunctionParser(FunctionDefinitionFinder functionDefinitionFinder) {
        this.functionDefinitionFinder = functionDefinitionFinder;
    }

    public List<Function> parse(String code) {
        String cleanedContent = cleanContent(code);
        CodeSearchScope codeSearchScope = new CodeSearchScope(cleanedContent);
        return parseFunctions(codeSearchScope);
    }

    private List<Function> parseFunctions(CodeSearchScope codeSearchScope) {
        List<Function> functions = new LinkedList<>();
        while (functionDefinitionFinder.hasNext(codeSearchScope)) {
            Function function = functionDefinitionFinder.next(codeSearchScope);
            functions.add(function);
        }
        return functions;
    }

    private String cleanContent(String code) {
        String removedComments = CommentRemover.removeComments(code);
        return StringCharRemover.remove(removedComments);
    }
}
