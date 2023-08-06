package earth.terrarium.prometheus.common.handlers.heading;

import com.teamresourceful.resourcefullib.common.utils.SaveHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HeadingHandler extends SaveHandler {

    private static final HeadingHandler CLIENT_SIDE = new HeadingHandler();

    private final Map<UUID, Heading> headings = new HashMap<>();

    public static HeadingHandler read(Level level) {
        return read(level, CLIENT_SIDE, HeadingHandler::new, "prometheus_headings");
    }

    public static boolean set(Player player, Heading heading) {
        if (!heading.hasPermission(player)) return false;
        read(player.level()).headings.put(player.getUUID(), heading);
        if (player instanceof HeadingEntityHook hook) {
            hook.prometheus$setHeadingAndUpdate(heading);
            HeadingEvents.sendToOnlinePlayers(player.getServer(), player, heading, heading.getDisplayName());
        }
        read(player.level()).setDirty();
        return true;
    }

    public static Heading get(Player player) {
        return read(player.level()).headings.getOrDefault(player.getUUID(), Heading.NONE);
    }

    @Override
    public void saveData(@NotNull CompoundTag tag) {
        headings.forEach((uuid, heading) -> tag.putString(uuid.toString(), heading.name()));
    }

    @Override
    public void loadData(CompoundTag tag) {
        tag.getAllKeys().forEach(key -> headings.put(UUID.fromString(key), Heading.fromName(tag.getString(key))));
    }
}
