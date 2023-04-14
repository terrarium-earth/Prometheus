package earth.terrarium.prometheus.common.handlers;

import com.mojang.authlib.GameProfile;
import earth.terrarium.prometheus.common.constants.ConstantComponents;
import earth.terrarium.prometheus.common.handlers.base.Handler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.time.temporal.TemporalUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MuteHandler extends Handler {

    private static final MuteHandler CLIENT_SIDE = new MuteHandler();

    private final Map<UUID, Instant> mutedPlayers = new HashMap<>();

    public static MuteHandler read(Level level) {
        return read(level, CLIENT_SIDE, MuteHandler::new, "prometheus_muted_players");
    }

    public static void mute(Level level, GameProfile profile, long length, TemporalUnit unit) {
        getMutedPlayers(level).put(profile.getId(), Instant.now().plus(length, unit));
        read(level).setDirty();
    }

    public static void unmute(Level level, GameProfile profile) {
        getMutedPlayers(level).remove(profile.getId());
        read(level).setDirty();
    }

    public static boolean canMessageGoThrough(ServerPlayer sender) {
        Instant time = getMutedPlayers(sender.level).get(sender.getUUID());
        if (time == null) return true;
        if (time.isBefore(Instant.now())) {
            unmute(sender.level, sender.getGameProfile());
            return true;
        }
        sender.sendSystemMessage(ConstantComponents.MUTED);
        return false;
    }

    public static Map<UUID, Instant> getMutedPlayers(Level level) {
        return read(level).mutedPlayers;
    }

    @Override
    public void saveData(@NotNull CompoundTag tag) {
        mutedPlayers.forEach((key, value) -> tag.putLong(key.toString(), value.getEpochSecond()));
    }

    @Override
    public void loadData(CompoundTag tag) {
        tag.getAllKeys().forEach(key -> mutedPlayers.put(UUID.fromString(key), Instant.ofEpochSecond(tag.getLong(key))));
    }
}
