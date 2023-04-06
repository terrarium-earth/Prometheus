package earth.terrarium.prometheus.common.handlers;

import earth.terrarium.prometheus.common.utils.ModUtils;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class WarpHandler extends SavedData {

    private static final WarpHandler CLIENT_SIDE = new WarpHandler();

    private final Map<String, GlobalPos> warps = new HashMap<>();

    public WarpHandler() {}

    public WarpHandler(CompoundTag tag) {
        tag.getAllKeys().forEach(key -> warps.put(key, ModUtils.fromTag(tag.getCompound(key))));
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag tag) {
        warps.forEach((key, value) -> tag.put(key, ModUtils.toTag(value)));
        return tag;
    }

    public static WarpHandler read(Level level) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return CLIENT_SIDE;
        }
        return read(serverLevel.getServer().overworld().getDataStorage());
    }

    public static WarpHandler read(DimensionDataStorage storage) {
        return storage.computeIfAbsent(WarpHandler::new, WarpHandler::new, "prometheus_warps");
    }

    public static void add(Player player, String name) {
        WarpHandler data = read(player.level);
        if (data.warps.containsKey(name)) {
            player.sendSystemMessage(Component.literal("Warp already exists"));
            return;
        }
        data.warps.put(name, GlobalPos.of(player.level.dimension(), player.blockPosition()));
        data.setDirty();
    }

    public static void remove(Player player, String name) {
        WarpHandler data = read(player.level);
        data.warps.remove(name);
        data.setDirty();
    }

    public static void teleport(Player player, String name) {
        WarpHandler data = read(player.level);
        GlobalPos pos = data.warps.get(name);
        if (pos == null) {
            player.sendSystemMessage(Component.literal("Warp not found"));
            return;
        }
        if (player instanceof ServerPlayer serverPlayer) {
            ServerLevel level = serverPlayer.server.getLevel(pos.dimension());
            if (level == null) {
                player.sendSystemMessage(Component.literal("Dimension not found"));
                return;
            }

            serverPlayer.teleportTo(level, pos.pos().getX(), pos.pos().getY(), pos.pos().getZ(), player.getYRot(), player.getXRot());
        }
    }

    public static Collection<String> getWarps(Player player) {
        WarpHandler data = read(player.level);
        return data.warps.keySet();
    }
}
