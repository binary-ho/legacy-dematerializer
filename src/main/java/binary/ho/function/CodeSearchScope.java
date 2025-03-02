package binary.ho.function;

public class CodeSearchScope {

    private final String code;
    private int startIndex = 0;

    public CodeSearchScope(String code) {
        this.code = code;
    }

    public boolean isOutOfScope() {
        return startIndex >= code.length();
    }

    public String getRemainingCode() {
        return code.substring(startIndex);
    }

    public int getScopeStart() {
        return startIndex;
    }

    public void advance(int value) {
        startIndex += value;
    }
}
