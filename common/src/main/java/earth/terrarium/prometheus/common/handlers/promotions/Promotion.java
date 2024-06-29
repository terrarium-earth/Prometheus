package earth.terrarium.prometheus.common.handlers.promotions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import earth.terrarium.prometheus.common.handlers.role.RoleHandler;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.UUID;

public record Promotion(
    Component name,
    long time,
    List<UUID> roles
) {

    public static final Codec<Promotion> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ComponentSerialization.CODEC.fieldOf("name").forGetter(Promotion::name),
        Codec.LONG.fieldOf("time").forGetter(Promotion::time),
        UUIDUtil.STRING_CODEC.listOf().fieldOf("roles").forGetter(Promotion::roles)
    ).apply(instance, Promotion::new));

    public void run(ServerPlayer player) {
        Object2BooleanMap<UUID> map = new Object2BooleanOpenHashMap<>();
        for (UUID id : roles) map.put(id, true);
        RoleHandler.changeRoles(player.level(), player.getUUID(), map);
    }

    public static Promotion fromId(String id, long time) {
        return new Promotion(
            Component.literal(id),
            time,
            List.of()
        );
    }
}
