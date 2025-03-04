package binary.ho.comment;

import binary.ho.string.StringCharBounds;

public class CommentRemover {

    private static final char SLASH = '/';
    private static final char STAR = '*';
    private static final char NEWLINE = '\n';

    public static String removeComments(String code) {
        if (code == null || code.isEmpty()) {
            return code;
        }

        StringBuilder result = new StringBuilder();
        StringCharBounds stringBounds = new StringCharBounds();

        int index = 0;
        while (index < code.length()) {
            char current = code.charAt(index);
            stringBounds.update(current);

            if (stringBounds.outOfString()) {
                if (isStartOfLineComment(code, index)) {
                    index = skipLineComment(code, index);
                    continue;
                }

                if (isStartOfBlockComment(code, index)) {
                    index = skipBlockComment(code, index);
                    continue;
                }
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
        return code.charAt(index) == SLASH && code.charAt(index + 1) == SLASH;
    }

    private static int skipLineComment(String code, int startIndex) {
        int index = startIndex;
        index += 2;
        while (index < code.length()) {
            if (code.charAt(index) == NEWLINE) {
                break;
            }
            index++;
        }
        return index;
    }

    private static boolean isStartOfBlockComment(String code, int index) {
        if (index + 1 >= code.length()) {
            return false;
        }
        return code.charAt(index) == SLASH && code.charAt(index + 1) == STAR;
    }

    private static int skipBlockComment(String code, int startIndex) {
        int index = startIndex;
        index += 2;
        while (index < code.length() - 1) {
            if (code.charAt(index) == STAR && code.charAt(index + 1) == SLASH) {
                index += 2;
                return index;
            }
            index++;
        }
        return code.length();
    }
}