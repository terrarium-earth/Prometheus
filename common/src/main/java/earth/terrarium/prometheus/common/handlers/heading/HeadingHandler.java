package earth.terrarium.prometheus.common.handlers.heading;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HeadingHandler extends SavedData {

    private static final HeadingHandler CLIENT_SIDE = new HeadingHandler();

    private final Map<UUID, Heading> headings = new HashMap<>();

    public HeadingHandler() {}

    public HeadingHandler(CompoundTag tag) {
        tag.getAllKeys().forEach(key -> headings.put(UUID.fromString(key), Heading.fromName(tag.getString(key))));
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag tag) {
        headings.forEach((uuid, heading) -> tag.putString(uuid.toString(), heading.name()));
        return tag;
    }

    public static HeadingHandler read(Level level) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return CLIENT_SIDE;
        }
        return read(serverLevel.getServer().overworld().getDataStorage());
    }

    public static HeadingHandler read(DimensionDataStorage storage) {
        return storage.computeIfAbsent(HeadingHandler::new, HeadingHandler::new, "prometheus_headings");
    }

    public static boolean set(Player player, Heading heading) {
        final HeadingHandler handler = read(player.level);
        if (heading.hasPermission(player)) {
            handler.headings.put(player.getUUID(), heading);
            if (player instanceof HeadingEntityHook hook) {
                hook.prometheus$setHeadingAndUpdate(heading);
            }
            handler.setDirty();
            return true;
        }
        return false;
    }

    public static Heading get(Player player) {
        final HeadingHandler handler = read(player.level);
        return handler.headings.getOrDefault(player.getUUID(), Heading.NONE);
    }
}
