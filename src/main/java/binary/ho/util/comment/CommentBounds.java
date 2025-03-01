package binary.ho.util.comment;

public class CommentBounds {

    private static final char SLASH = '/';
    private static final char STAR = '*';
    private static final char NEWLINE = '\n';

    private boolean inBlockComment = false;
    private boolean inLineComment = false;

    private final String code;

    public CommentBounds(String code) {
        this.code = code;
    }

    public boolean isStartOfBlockComment(int index) {
        char current = code.charAt(index);
        char next = getNext(index);

        return outOfComment() && current == SLASH && next == STAR;
    }

    public boolean isEndOfBlockComment(int index) {
        char current = code.charAt(index);
        char next = getNext(index);

        return inBlockComment && current == STAR && next == SLASH;
    }

    public boolean isStartOfLineComment(int index) {
        char current = code.charAt(index);
        char next = getNext(index);

        return outOfComment() && current == SLASH && next == SLASH;
    }

    public boolean isEndOfLineComment(int index) {
        char current = code.charAt(index);

        return inLineComment && current == NEWLINE;
    }

    public boolean outOfComment() {
        return !inBlockComment && !inLineComment;
    }

    public void enterBlockComment() {
        inBlockComment = true;
    }

    public void exitBlockComment() {
        inBlockComment = false;
    }

    public void enterLineComment() {
        inLineComment = true;
    }

    public void exitLineComment() {
        inLineComment = false;
    }

    private char getNext(int index) {
        if (index + 1 >= code.length()) {
            return '\0';
        }
        return code.charAt(index + 1);
    }
}