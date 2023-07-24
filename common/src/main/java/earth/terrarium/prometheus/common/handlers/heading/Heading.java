package earth.terrarium.prometheus.common.handlers.heading;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.teamresourceful.resourcefullib.common.utils.CommonUtils;
import earth.terrarium.prometheus.api.permissions.PermissionApi;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;

public enum Heading {
    NONE(-1, -1),
    AFK(8, 8),
    DND(0, 8),
    MUSIC(-1, -1),
    RECORDING(0, 0),
    STREAMING(8, 0);

    public static final List<Heading> VALUES = List.of(values());

    private final String translation;
    private final String name;
    private final int u;
    private final int v;

    Heading(int u, int v) {
        this.translation = "prometheus.heading." + this.name().toLowerCase(Locale.ROOT);
        this.name = "prometheus.heading.name." + this.name().toLowerCase(Locale.ROOT);
        this.u = u;
        this.v = v;
    }

    public Component getTranslation(Object... args) {
        return CommonUtils.serverTranslatable(translation, args);
    }

    public boolean hasIcon() {
        return this.u != -1 && this.v != -1;
    }

    public int getU() {
        return this.u;
    }

    public int getV() {
        return this.v;
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
