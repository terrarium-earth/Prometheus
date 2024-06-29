package earth.terrarium.prometheus.common.roles;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.bytecodecs.base.ByteCodec;
import com.teamresourceful.bytecodecs.base.object.ObjectByteCodec;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.api.roles.options.RoleOption;
import earth.terrarium.prometheus.api.roles.options.RoleOptionSerializer;

public record TeleportOptions(int expire, int rtpCooldown, int rtpDistance) implements RoleOption<TeleportOptions> {

    public static final RoleOptionSerializer<TeleportOptions> SERIALIZER = RoleOptionSerializer.of(
            Prometheus.id("teleport"),
            1,
            RecordCodecBuilder.create(instance -> instance.group(
                    Codec.INT.fieldOf("tpaExpire").orElse(30000).forGetter(TeleportOptions::expire),
                    Codec.INT.fieldOf("rtpCooldown").orElse(60000).forGetter(TeleportOptions::rtpCooldown),
                    Codec.INT.fieldOf("rtpDistance").orElse(3000).forGetter(TeleportOptions::rtpDistance)
            ).apply(instance, TeleportOptions::new)),
            ObjectByteCodec.create(
                    ByteCodec.VAR_INT.fieldOf(TeleportOptions::expire),
                    ByteCodec.VAR_INT.fieldOf(TeleportOptions::rtpCooldown),
                    ByteCodec.VAR_INT.fieldOf(TeleportOptions::rtpDistance),
                    TeleportOptions::new
            ),
            new TeleportOptions(30000, 60000, 3000)
    );

    @Override
    public RoleOptionSerializer<TeleportOptions> serializer() {
        return SERIALIZER;
    }
}
