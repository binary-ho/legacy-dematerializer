package binary.ho.string;

public class StringCharRemover {

    public static String remove(String code) {
        if (code == null || code.isEmpty()) {
            return "empty";
        }

        StringCharBounds bounds = new StringCharBounds(code);
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < code.length(); i++) {
            char current = code.charAt(i);
            if (bounds.outOfString() && bounds.isNotQuote(i)) {
                stringBuilder.append(current);
                continue;
            }
            bounds.update(i);
        }
        return stringBuilder.toString();
    }
}
