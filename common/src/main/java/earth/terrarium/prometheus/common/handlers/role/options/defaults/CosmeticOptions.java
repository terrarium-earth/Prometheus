package earth.terrarium.prometheus.common.handlers.role.options.defaults;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefullib.common.color.Color;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.api.roles.options.RoleOption;
import earth.terrarium.prometheus.api.roles.options.RoleOptionSerializer;
import net.minecraft.resources.ResourceLocation;

public record CosmeticOptions(String display, String icon, Color color) implements RoleOption<CosmeticOptions> {

    public static final RoleOptionSerializer<CosmeticOptions> SERIALIZER = RoleOptionSerializer.of(
            new ResourceLocation(Prometheus.MOD_ID, "cosmetics"),
            1,
            RecordCodecBuilder.create(instance -> instance.group(
                    Codec.STRING.fieldOf("display").orElse("New Role").forGetter(CosmeticOptions::display),
                    Codec.STRING.fieldOf("icon").orElse("?").forGetter(options -> String.valueOf(options.icon())),
                    Color.CODEC.fieldOf("color").orElse(Color.DEFAULT).forGetter(CosmeticOptions::color)
            ).apply(instance, CosmeticOptions::new)),
            new CosmeticOptions("New Role", "?", Color.DEFAULT)
    );

    @Override
    public RoleOptionSerializer<CosmeticOptions> serializer() {
        return SERIALIZER;
    }
}
