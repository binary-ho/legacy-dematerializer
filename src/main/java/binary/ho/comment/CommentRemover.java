package binary.ho.comment;

public class CommentRemover {

    private static final char SLASH = '/';
    private static final char STAR = '*';
    public static final char END_OF_LINE = '\n';

    public static String removeComments(String code) {
        StringBuilder result = new StringBuilder();
        int index = 0;
        while (index < code.length()) {
            char current = code.charAt(index);
            if (isStartOfLineComment(code, index)) {
                index = getEndOfLineIndex(code, index);
                continue;
            }

            if (isStartOfBlockComment(code, index)) {
                index = getEndOfBlockCommentIndex(code, index);
                continue;
            }

            result.append(current);
            index++;
        }
        return result.toString();
    }

    private static boolean isStartOfLineComment(String code, int index) {
        if (index + 1 >= code.length()) {
            return false;
        }
        char current = code.charAt(index);
        char next = code.charAt(index + 1);
        return current == SLASH && next == SLASH;
    }

    private static int getEndOfLineIndex(String code, int index) {
        while (index < code.length() && code.charAt(index) != END_OF_LINE) {
            index++;
        }
        return index;
    }

    private static boolean isStartOfBlockComment(String code, int index) {
        if (index + 1 >= code.length()) {
            return false;
        }
        char current = code.charAt(index);
        char next = code.charAt(index + 1);
        return current == SLASH && next == STAR;
    }

    private static int getEndOfBlockCommentIndex(String code, int index) {
        index += 2;
        while (isNotEndOfBlockComment(code, index)) {
            index++;
        }
        return Math.min(index + 2, code.length());
    }

    private static boolean isNotEndOfBlockComment(String code, int index) {
        if (index + 1 >= code.length()) {
            return false;
        }
        return !(code.charAt(index) == STAR && code.charAt(index + 1) == SLASH);
    }
}
