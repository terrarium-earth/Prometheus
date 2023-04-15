package earth.terrarium.prometheus.common.roles;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.api.roles.options.RoleOption;
import earth.terrarium.prometheus.api.roles.options.RoleOptionSerializer;
import net.minecraft.resources.ResourceLocation;

public record TeleportOptions(int expire, int rtpCooldown, int rtpDistance) implements RoleOption<TeleportOptions> {

    public static final RoleOptionSerializer<TeleportOptions> SERIALIZER = RoleOptionSerializer.of(
            new ResourceLocation(Prometheus.MOD_ID, "teleport"),
            1,
            RecordCodecBuilder.create(instance -> instance.group(
                    Codec.INT.fieldOf("tpaExpire").orElse(30000).forGetter(TeleportOptions::expire),
                    Codec.INT.fieldOf("rtpCooldown").orElse(60000).forGetter(TeleportOptions::rtpCooldown),
                    Codec.INT.fieldOf("rtpDistance").orElse(3000).forGetter(TeleportOptions::rtpDistance)
            ).apply(instance, TeleportOptions::new)),
            new TeleportOptions(30000, 60000, 3000)
    );

    @Override
    public RoleOptionSerializer<TeleportOptions> serializer() {
        return SERIALIZER;
    }
}
