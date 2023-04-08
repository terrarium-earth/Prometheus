package earth.terrarium.prometheus.common.handlers.role.options.defaults;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefullib.common.color.Color;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.api.roles.options.RoleOption;
import earth.terrarium.prometheus.api.roles.options.RoleOptionSerializer;
import net.minecraft.resources.ResourceLocation;

public record DisplayOptions(String display, char icon, Color color) implements RoleOption<DisplayOptions> {

    public DisplayOptions(String display, String icon, Color color) {
        this(display, icon.charAt(0), color);
    }

    public static final RoleOptionSerializer<DisplayOptions> SERIALIZER = RoleOptionSerializer.of(
            new ResourceLocation(Prometheus.MOD_ID, "display"),
            1,
            RecordCodecBuilder.create(instance -> instance.group(
                    Codec.STRING.fieldOf("display").forGetter(DisplayOptions::display),
                    Codec.STRING.fieldOf("icon").forGetter(options -> String.valueOf(options.icon())),
                    Color.CODEC.fieldOf("color").forGetter(DisplayOptions::color)
            ).apply(instance, DisplayOptions::new)),
            new DisplayOptions("New Role", '\uD83D', Color.DEFAULT)
    );

    @Override
    public RoleOptionSerializer<DisplayOptions> serializer() {
        return SERIALIZER;
    }
}
