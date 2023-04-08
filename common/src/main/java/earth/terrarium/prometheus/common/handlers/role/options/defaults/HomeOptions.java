package earth.terrarium.prometheus.common.handlers.role.options.defaults;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.api.roles.options.RoleOption;
import earth.terrarium.prometheus.api.roles.options.RoleOptionSerializer;
import net.minecraft.resources.ResourceLocation;

public record HomeOptions(int max) implements RoleOption<HomeOptions> {

    public static final RoleOptionSerializer<HomeOptions> SERIALIZER = RoleOptionSerializer.of(
            new ResourceLocation(Prometheus.MOD_ID, "homes"),
            1,
            RecordCodecBuilder.create(instance -> instance.group(
                    Codec.INT.fieldOf("max").forGetter(HomeOptions::max)
            ).apply(instance, HomeOptions::new)),
            new HomeOptions(5)
    );

    @Override
    public RoleOptionSerializer<HomeOptions> serializer() {
        return SERIALIZER;
    }
}
