package binary.ho.comment;

public class CommentRemover {

    public static String removeComments(String code) {
        StringBuilder result = new StringBuilder();
        CommentBounds commentBounds = new CommentBounds(code);

        for (int index = 0; index < code.length(); index++) {
            char current = code.charAt(index);
            if (commentBounds.isStartOfBlockComment(index)) {
                commentBounds.enterBlockComment();
                index++;
                continue;
            }
            if (commentBounds.isEndOfBlockComment(index)) {
                commentBounds.exitBlockComment();
                index++;
                continue;
            }
            if (commentBounds.isStartOfLineComment(index)) {
                commentBounds.enterLineComment();
                index++;
                continue;
            }

            if (commentBounds.isEndOfLineComment(index)) {
                commentBounds.exitLineComment();
            }

            if (commentBounds.outOfComment()) {
                result.append(current);
            }
        }

        return result.toString();
    }
}
