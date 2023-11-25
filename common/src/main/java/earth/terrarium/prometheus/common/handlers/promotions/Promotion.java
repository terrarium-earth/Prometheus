package earth.terrarium.prometheus.common.handlers.promotions;

import earth.terrarium.prometheus.common.handlers.role.RoleHandler;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.UUID;

public record Promotion(
    Component name,
    long time,
    List<UUID> roles
) {

    public void run(ServerPlayer player) {
        Object2BooleanMap<UUID> map = new Object2BooleanOpenHashMap<>();
        for (UUID id : roles) map.put(id, true);
        RoleHandler.changeRoles(player.level(), player.getUUID(), map);
    }

    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        tag.putString("name", Component.Serializer.toJson(name));
        tag.putLong("time", time);
        ListTag roles = new ListTag();
        this.roles.forEach(uuid -> roles.add(StringTag.valueOf(uuid.toString())));
        tag.put("roles", roles);
        return tag;
    }

    public static Promotion fromTag(CompoundTag tag) {
        return new Promotion(
            Component.Serializer.fromJson(tag.getString("name")),
            tag.getLong("time"),
            tag.getList("roles", 8).stream()
                .map(Tag::getAsString)
                .map(UUID::fromString)
                .toList()
        );
    }

    public static Promotion fromId(String id, long time) {
        return new Promotion(
            Component.literal(id),
            time,
            List.of()
        );
    }
}
