package earth.terrarium.prometheus.api;

public enum TriState {
    TRUE,
    FALSE,
    UNDEFINED;

    public static TriState of(boolean value) {
        return value ? TRUE : FALSE;
    }

    public static TriState of(Boolean value) {
        return value == null ? UNDEFINED : of(value.booleanValue());
    }

    public boolean isTrue() {
        return this == TRUE;
    }

    public boolean isFalse() {
        return this == FALSE;
    }

    public boolean isUndefined() {
        return this == UNDEFINED;
    }

    public boolean isDefined() {
        return this != UNDEFINED;
    }

    public static TriState of(Number number) {
        if (number == null || number.longValue() >= 2) {
            return UNDEFINED;
        }
        return of(number.longValue() == 0);
    }
}
