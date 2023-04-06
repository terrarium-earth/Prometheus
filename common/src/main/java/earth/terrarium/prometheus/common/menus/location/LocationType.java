package earth.terrarium.prometheus.common.menus.location;

import java.util.Locale;

public enum LocationType {
    HOME("homes", "home"),
    WARP("warps", "warp");

    private final String editPrefix;
    private final String tpPrefix;

    LocationType(String editPrefix, String tpPrefix) {
        this.editPrefix = editPrefix;
        this.tpPrefix = tpPrefix;
    }

    public String editPrefix() {
        return editPrefix;
    }

    public String tpPrefix() {
        return tpPrefix;
    }

    public String getId() {
        return name().toLowerCase(Locale.ROOT);
    }
}