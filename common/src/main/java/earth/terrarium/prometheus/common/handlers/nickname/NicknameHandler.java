package earth.terrarium.prometheus.common.handlers.nickname;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NicknameHandler extends SavedData {

    private static final NicknameHandler CLIENT_SIDE = new NicknameHandler();

    private final Map<UUID, Nickname> names = new HashMap<>();

    public NicknameHandler() {}

    public NicknameHandler(CompoundTag tag) {
        tag.getAllKeys().forEach(key -> names.put(UUID.fromString(key), Nickname.of(tag.getCompound(key))));
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag tag) {
        names.forEach((key, value) -> tag.put(key.toString(), value.toTag()));
        return tag;
    }

    public static NicknameHandler read(Level level) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return CLIENT_SIDE;
        }
        return read(serverLevel.getServer().overworld().getDataStorage());
    }

    public static NicknameHandler read(DimensionDataStorage storage) {
        return storage.computeIfAbsent(NicknameHandler::new, NicknameHandler::new, "prometheus_nicknames");
    }

    public static void set(Player player, String name) {
        set(player, Component.literal(name));
    }

    public static void set(Player player, Component name) {
        NicknameHandler data = read(player.level);
        data.names.put(player.getUUID(), Nickname.of(player, name));
        data.setDirty();
        if (player instanceof NickedEntityHook hook) {
            hook.prometheus$setNickname(name);
        }
    }

    public static void remove(Player player) {
        NicknameHandler data = read(player.level);
        data.names.remove(player.getUUID());
        data.setDirty();
        if (player instanceof NickedEntityHook hook) {
            hook.prometheus$setNickname(null);
        }
    }

    @Nullable
    public static Component get(Player player) {
        NicknameHandler data = read(player.level);
        Nickname nickname = data.names.get(player.getUUID());
        return nickname == null ? null : nickname.component;
    }

    public static Collection<Nickname> getNicknames(Level level) {
        NicknameHandler data = read(level);
        return data.names.values();
    }

    public record Nickname(String name, Component component) {
        public static Nickname of(Player player, Component component) {
            return new Nickname(player.getName().getString(), component);
        }

        public static Nickname of(CompoundTag tag) {
            return new Nickname(tag.getString("name"), Component.Serializer.fromJson(tag.getString("component")));
        }

        public CompoundTag toTag() {
            CompoundTag tag = new CompoundTag();
            tag.putString("name", name);
            tag.putString("component", Component.Serializer.toJson(component));
            return tag;
        }
    }
}
