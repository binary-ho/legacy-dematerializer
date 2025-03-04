package binary.ho.writer;

import java.util.Collection;

public enum ChildPosition {

    TOP_CHILD("─── "),
    MIDDLE_CHILD("├── "),
    BOTTOM_CHILD("└── "),
    NO_PARENT(""),
    ;

    private final String branch;

    ChildPosition(String branch) {
        this.branch = branch;
    }

    public String getBranch() {
        return branch;
    }

    public static ChildPosition getPosition(int index, Collection<?> collection) {
        int lastIndex = collection.size() - 1;
        if (index == 0) {
            return TOP_CHILD;
        } else if (index == lastIndex) {
            return BOTTOM_CHILD;
        } else {
            return MIDDLE_CHILD;
        }
    }
}
