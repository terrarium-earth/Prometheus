package earth.terrarium.prometheus.common.handlers.nickname;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.world.entity.player.Player;

public record Nickname(String name, Component component) {

    public static final Codec<Nickname> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.STRING.fieldOf("name").forGetter(Nickname::name),
        ComponentSerialization.CODEC.optionalFieldOf("component", CommonComponents.SPACE).forGetter(Nickname::component)
    ).apply(instance, Nickname::new));

    public static final Nickname EMPTY = new Nickname("", null);

    public static Nickname of(Player player, Component component) {
        return new Nickname(player.getName().getString(), component);
    }
}