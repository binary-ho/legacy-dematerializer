package binary.ho.util.comment;

public class StringBounds {

    private static final char QUOTE_DOUBLE = '"';
    private static final char QUOTE_SINGLE = '\'';
    private static final char ESCAPE = '\\';

    private boolean inString = false;
    private char stringQuote = 0;

    private final String code;

    public StringBounds(String code) {
        this.code = code;
    }

    public void update(int index) {
        char current = code.charAt(index);
        if (isNotQuote(current) || isEscape(index, code)) {
            return;
        }

        if (!inString) {
            inString = true;
            stringQuote = current;
        } else if (current == stringQuote) {
            inString = false;
        }
    }

    private boolean isNotQuote(char current) {
        return current != QUOTE_DOUBLE && current != QUOTE_SINGLE;
    }

    private boolean isEscape(int index, String code) {
        return index != 0 && code.charAt(index - 1) == ESCAPE;
    }

    public boolean outOfString() {
        return !inString;
    }
}
