package earth.terrarium.prometheus.client.utils;

public interface CursorScreen {

    void setCursor(Cursor cursor);

    enum Cursor {
        DEFAULT,
        POINTER,
        DISABLED,
        TEXT,
    }
}
