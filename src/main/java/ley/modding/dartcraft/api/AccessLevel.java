package ley.modding.dartcraft.api;

public enum AccessLevel {
    OPEN("Open"),
    RESTRICTED("Restricted"),
    CLOSED("Closed");

    private static final AccessLevel[] NEXT_LOOKUP = { RESTRICTED, CLOSED, OPEN };

    public final String displayName;

    private AccessLevel(String displayName) {
        this.displayName = displayName;
    }

    public AccessLevel next() {
        return NEXT_LOOKUP[this.ordinal()];
    }
}
