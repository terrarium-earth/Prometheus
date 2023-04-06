package earth.terrarium.prometheus.common.handlers;

import com.mojang.authlib.GameProfile;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.time.temporal.TemporalUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MuteHandler extends SavedData {

    private static final MuteHandler CLIENT_SIDE = new MuteHandler();

    private final Map<UUID, Instant> mutedPlayers = new HashMap<>();

    public MuteHandler() {}

    public MuteHandler(CompoundTag tag) {
        for (String key : tag.getAllKeys()) {
            mutedPlayers.put(UUID.fromString(key), Instant.ofEpochSecond(tag.getLong(key)));
        }
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag tag) {
        mutedPlayers.forEach((key, value) -> tag.putLong(key.toString(), value.getEpochSecond()));
        return tag;
    }

    public static MuteHandler read(Level level) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return CLIENT_SIDE;
        }
        return read(serverLevel.getServer().overworld().getDataStorage());
    }

    public static MuteHandler read(DimensionDataStorage storage) {
        return storage.computeIfAbsent(MuteHandler::new, MuteHandler::new, "prometheus_muted_players");
    }

    public static void mutePlayer(Level level, GameProfile profile, long length, TemporalUnit unit) {
        MuteHandler data = read(level);
        data.mutedPlayers.put(profile.getId(), Instant.now().plus(length, unit));
        data.setDirty();
    }

    public static void mutePlayer(Player player, long length, TemporalUnit unit) {
        MuteHandler data = read(player.level);
        data.mutedPlayers.put(player.getUUID(), Instant.now().plus(length, unit));
        data.setDirty();
    }

    public static void unmutePlayer(Level level, GameProfile profile) {
        MuteHandler data = read(level);
        data.mutedPlayers.remove(profile.getId());
        data.setDirty();
    }

    public static void unmutePlayer(Player player) {
        MuteHandler data = read(player.level);
        data.mutedPlayers.remove(player.getUUID());
        data.setDirty();
    }

    public static boolean isMuted(Player player) {
        MuteHandler data = read(player.level);
        Instant muteTime = data.mutedPlayers.get(player.getUUID());
        if (muteTime == null) {
            return false;
        }
        if (muteTime.isBefore(Instant.now())) {
            unmutePlayer(player);
            return false;
        }
        return true;
    }

    public static boolean canMessageGoThrough(ServerPlayer sender) {
        if (isMuted(sender)) {
            sender.sendSystemMessage(Component.literal("You are muted!"));
            return false;
        }
        return true;
    }
}
