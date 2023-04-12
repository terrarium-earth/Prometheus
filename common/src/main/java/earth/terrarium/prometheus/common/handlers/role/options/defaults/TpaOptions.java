package earth.terrarium.prometheus.common.handlers.role.options.defaults;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.api.roles.options.RoleOption;
import earth.terrarium.prometheus.api.roles.options.RoleOptionSerializer;
import net.minecraft.resources.ResourceLocation;

public record TpaOptions(int expire) implements RoleOption<TpaOptions> {

    public static final RoleOptionSerializer<TpaOptions> SERIALIZER = RoleOptionSerializer.of(
            new ResourceLocation(Prometheus.MOD_ID, "tpa"),
            1,
            RecordCodecBuilder.create(instance -> instance.group(
                    Codec.INT.fieldOf("expire").orElse(30000).forGetter(TpaOptions::expire)
            ).apply(instance, TpaOptions::new)),
            new TpaOptions(30000)
    );

    @Override
    public RoleOptionSerializer<TpaOptions> serializer() {
        return SERIALIZER;
    }
}
