package binary.ho.string;

public class StringCharBounds {

    private static final char STRING_QUOTE = '\"';
    private static final char CHAR_QUOTE = '\'';
    private static final char ESCAPE = '\\';

    private boolean inString = false;
    private boolean inChar = false;
    private boolean escaped = false;

    private final String code;

    public StringCharBounds(String code) {
        this.code = code;
    }

    public void update(int index) {
        char current = code.charAt(index);
        if (outOfString()) {
            if (current == STRING_QUOTE) {
                inString = true;
            }

            if (current == CHAR_QUOTE) {
                inChar = true;
            }
            return;
        }

        if (escaped) {
            escaped = false;
            return;
        }

        if (current == ESCAPE) {
            escaped = true;
            return;
        }

        if ((inString && current == STRING_QUOTE) || (inChar && current == CHAR_QUOTE)) {
            inString = false;
            inChar = false;
        }
    }

    public boolean isNotQuote(int index) {
        char current = code.charAt(index);
        return current != STRING_QUOTE && current != CHAR_QUOTE;
    }

    public boolean outOfString() {
        return !inString && !inChar;
    }
}
