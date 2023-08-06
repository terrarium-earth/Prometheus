package earth.terrarium.prometheus.common.handlers.nickname;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public record Nickname(String name, Component component) {

    public static final Nickname EMPTY = new Nickname("", null);


    public static Nickname of(Player player, Component component) {
        return new Nickname(player.getName().getString(), component);
    }

    public static Nickname of(CompoundTag tag) {
        Component component = Component.Serializer.fromJson(tag.getString("component"));
        if (component != null && component.getString().isBlank()) {
            component = CommonComponents.SPACE;
        }
        return new Nickname(tag.getString("name"), component);
    }

    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        tag.putString("name", name);
        tag.putString("component", Component.Serializer.toJson(component));
        return tag;
    }
}