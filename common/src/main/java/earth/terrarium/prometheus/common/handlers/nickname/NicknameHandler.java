package earth.terrarium.prometheus.common.handlers.nickname;

import com.mojang.serialization.Codec;
import com.teamresourceful.resourcefullib.common.utils.files.CodecSavedData;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NicknameHandler {

    private static final CodecSavedData.Factory<Map<UUID, Nickname>> DATA = CodecSavedData
        .create(Codec.unboundedMap(UUIDUtil.STRING_CODEC, Nickname.CODEC), "prometheus_nicknames")
        .defaultValue(HashMap::new)
        .global()
        .clean();

    public static void set(ServerPlayer player, Component name) {
        var data = DATA.create(player.serverLevel());
        data.get().put(player.getUUID(), Nickname.of(player, name));
        data.setDirty();
        if (player instanceof NickedEntityHook hook) {
            hook.prometheus$setNickname(name);
        }
    }

    public static void remove(ServerPlayer player) {
        var data = DATA.create(player.serverLevel());
        data.get().remove(player.getUUID());
        data.setDirty();
        if (player instanceof NickedEntityHook hook) {
            hook.prometheus$setNickname(null);
        }
    }

    @Nullable
    public static Component get(ServerPlayer player) {
        var data = DATA.create(player.serverLevel());
        return data.get()
            .getOrDefault(player.getUUID(), Nickname.EMPTY)
            .component();
    }

    public static Map<UUID, Nickname> names(ServerLevel level) {
        return DATA.create(level).get();
    }
}
