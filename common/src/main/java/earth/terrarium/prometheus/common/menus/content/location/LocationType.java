package earth.terrarium.prometheus.common.menus.content.location;

import earth.terrarium.prometheus.common.constants.ConstantComponents;
import net.minecraft.network.chat.Component;

import java.util.Locale;

public enum LocationType {
    HOME("homes", "home", ConstantComponents.HOMES_UI_TITLE),
    WARP("warps", "warp", ConstantComponents.WARPS_UI_TITLE);

    private final String editPrefix;
    private final String tpPrefix;
    private final Component title;

    LocationType(String editPrefix, String tpPrefix, Component title) {
        this.editPrefix = editPrefix;
        this.tpPrefix = tpPrefix;
        this.title = title;
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

    public Component title() {
        return title;
    }
}