package binary.ho.string;

public class StringCharBounds {

    private static final char STRING_QUOTE = '"';
    private static final char CHAR_QUOTE = '\'';
    private static final char ESCAPE = '\\';

    private boolean inString = false;
    private boolean inChar = false;
    private boolean escaped = false;

    public StringCharBounds() {
    }

    public void update(char current) {
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

    public boolean isNotQuote(char current) {
        return current != STRING_QUOTE && current != CHAR_QUOTE;
    }

    public boolean outOfString() {
        return !inString && !inChar;
    }
}
