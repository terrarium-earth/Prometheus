package earth.terrarium.prometheus.common.handlers.nickname;

import earth.terrarium.prometheus.common.handlers.base.Handler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NicknameHandler extends Handler {

    private static final NicknameHandler CLIENT_SIDE = new NicknameHandler();

    private final Map<UUID, Nickname> names = new HashMap<>();

    public static NicknameHandler read(Level level) {
        return read(level, CLIENT_SIDE, NicknameHandler::new, "prometheus_nicknames");
    }

    public static void set(ServerPlayer player, Component name) {
        NicknameHandler data = read(player.level);
        names(player).put(player.getUUID(), Nickname.of(player, name));
        data.setDirty();
        if (player instanceof NickedEntityHook hook) {
            hook.prometheus$setNickname(name);
        }
    }

    public static void remove(ServerPlayer player) {
        NicknameHandler data = read(player.level);
        names(player).remove(player.getUUID());
        data.setDirty();
        if (player instanceof NickedEntityHook hook) {
            hook.prometheus$setNickname(null);
        }
    }

    @Nullable
    public static Component get(ServerPlayer player) {
        return names(player)
            .getOrDefault(player.getUUID(), Nickname.EMPTY)
            .component();
    }

    public static Map<UUID, Nickname> names(Level level) {
        return read(level).names;
    }

    public static Map<UUID, Nickname> names(ServerPlayer player) {
        return names(player.level);
    }

    @Override
    public void saveData(@NotNull CompoundTag tag) {
        names.forEach((key, value) -> tag.put(key.toString(), value.toTag()));
    }

    @Override
    public void loadData(CompoundTag tag) {
        tag.getAllKeys().forEach(key -> names.put(UUID.fromString(key), Nickname.of(tag.getCompound(key))));
    }
}
