package earth.terrarium.prometheus.common.handlers.role.options.defaults;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefullib.common.codecs.CodecExtras;
import com.teamresourceful.resourcefullib.common.color.Color;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.api.roles.options.RoleOption;
import earth.terrarium.prometheus.api.roles.options.RoleOptionSerializer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

public record DisplayOption(MutableComponent display, Color color) implements RoleOption<DisplayOption> {

    public static final RoleOptionSerializer<DisplayOption> SERIALIZER = RoleOptionSerializer.of(
            new ResourceLocation(Prometheus.MOD_ID, "display"),
            1,
            RecordCodecBuilder.create(instance -> instance.group(
                    CodecExtras.passthrough(Component.Serializer::toJsonTree, Component.Serializer::fromJson).fieldOf("display").forGetter(DisplayOption::display),
                    Color.CODEC.fieldOf("color").forGetter(DisplayOption::color)
            ).apply(instance, DisplayOption::new)),
            new DisplayOption(Component.literal("Error No Name Set"), Color.DEFAULT)
    );

    @Override
    public RoleOptionSerializer<DisplayOption> serializer() {
        return SERIALIZER;
    }
}
