package earth.terrarium.prometheus.common.handlers.heading;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import earth.terrarium.prometheus.api.permissions.PermissionApi;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;

public enum Heading {
    NONE(""),
    AFK("§e⌚"),
    DND("§c✖"),
    MUSIC("§9♬"),
    RECORDING("§c●"),
    STREAMING("§5●");

    public static final List<Heading> VALUES = List.of(values());

    private final String translation;
    private final String name;
    private final String icon;

    Heading(String icon) {
        this.translation = "prometheus.heading." + this.name().toLowerCase(Locale.ROOT);
        this.name = "prometheus.heading.name." + this.name().toLowerCase(Locale.ROOT);
        this.icon = icon;
    }

    public Component getTranslation(Object... args) {
        return Component.translatable(translation, args);
    }

    public String getIcon() {
        return icon;
    }

    public Component getDisplayName() {
        return Component.translatable(name);
    }

    public String permission() {
        return "headings." + this.name().toLowerCase(Locale.ROOT);
    }

    public boolean hasPermission(@Nullable Player player) {
        if (this == NONE) return true;
        return player != null && PermissionApi.API.getPermission(player, this.permission()).isTrue();
    }

    public static Heading fromId(byte id) {
        try {
            return values()[id];
        } catch (Exception e) {
            return NONE;
        }
    }

    public static Heading fromName(String name) {
        try {
            return valueOf(name.toUpperCase(Locale.ROOT));
        } catch (Exception e) {
            return NONE;
        }
    }

    @Nullable
    public static Heading fromCommand(CommandContext<CommandSourceStack> context) {
        try {
            return valueOf(StringArgumentType.getString(context, "name").toUpperCase(Locale.ROOT));
        } catch (Exception e) {
            return null;
        }
    }

    public static Component getInitalComponent(Heading heading) {
        return switch (heading) {
            case NONE -> null;
            case MUSIC -> heading.getTranslation("");
            default -> heading.getTranslation();
        };
    }

}
