package earth.terrarium.prometheus.common.roles;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.bytecodecs.base.ByteCodec;
import com.teamresourceful.bytecodecs.base.object.ObjectByteCodec;
import com.teamresourceful.resourcefullib.common.color.Color;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.api.roles.options.RoleOption;
import earth.terrarium.prometheus.api.roles.options.RoleOptionSerializer;

public record CosmeticOptions(String display, String icon, Color color) implements RoleOption<CosmeticOptions> {

    public static final RoleOptionSerializer<CosmeticOptions> SERIALIZER = RoleOptionSerializer.of(
            Prometheus.id("cosmetics"),
            1,
            RecordCodecBuilder.create(instance -> instance.group(
                    Codec.STRING.fieldOf("display").orElse("New Role").forGetter(CosmeticOptions::display),
                    Codec.STRING.fieldOf("icon").orElse("?").forGetter(CosmeticOptions::icon),
                    Color.CODEC.fieldOf("color").orElse(Color.DEFAULT).forGetter(CosmeticOptions::color)
            ).apply(instance, CosmeticOptions::new)),
            ObjectByteCodec.create(
                    ByteCodec.STRING.fieldOf(CosmeticOptions::display),
                    ByteCodec.STRING.fieldOf(CosmeticOptions::icon),
                    Color.BYTE_CODEC.fieldOf(CosmeticOptions::color),
                    CosmeticOptions::new
            ),
            new CosmeticOptions("New Role", "?", Color.DEFAULT)
    );

    @Override
    public RoleOptionSerializer<CosmeticOptions> serializer() {
        return SERIALIZER;
    }
}
