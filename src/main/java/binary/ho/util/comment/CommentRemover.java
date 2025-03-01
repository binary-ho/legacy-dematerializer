package binary.ho.util.comment;

public class CommentRemover {

    public String removeComments(String code) {
        StringBuilder result = new StringBuilder();
        StringBounds stringBounds = new StringBounds(code);
        CommentBounds commentBounds = new CommentBounds(code);

        for (int index = 0; index < code.length(); index++) {
            char current = code.charAt(index);

            if (commentBounds.outOfComment()) {
                stringBounds.update(index);
            }

            if (stringBounds.outOfString()) {
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
            }

            if (commentBounds.outOfComment()) {
                result.append(current);
            }
        }

        return result.toString();
    }
}
