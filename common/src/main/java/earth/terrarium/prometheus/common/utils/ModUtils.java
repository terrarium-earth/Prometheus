package earth.terrarium.prometheus.common.utils;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public final class ModUtils {

    public static GlobalPos fromTag(CompoundTag tag) {
        BlockPos pos = NbtUtils.readBlockPos(tag.getCompound("pos"));
        ResourceKey<Level> dimension = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(tag.getString("dimension")));
        return GlobalPos.of(dimension, pos);
    }

    public static CompoundTag toTag(GlobalPos pos) {
        CompoundTag tag = new CompoundTag();
        tag.put("pos", NbtUtils.writeBlockPos(pos.pos()));
        tag.putString("dimension", pos.dimension().location().toString());
        return tag;
    }

    public static <T extends ArgumentBuilder<CommandSourceStack, T>> T ofPlayers(ArgumentBuilder<CommandSourceStack, T> builder, BiConsumer<CommandContext<CommandSourceStack>, Player> playerConsumer) {
        return builder.then(Commands.argument("players", EntityArgument.players())
            .executes(context -> {
                EntityArgument.getPlayers(context, "players").forEach(player -> playerConsumer.accept(context, player));
                return 1;
            })
        ).executes(context -> {
            if (context.getSource().getEntity() instanceof Player player) {
                playerConsumer.accept(context, player);
            }
            return 1;
        });
    }

    public static void swapItems(Player player, EquipmentSlot first, EquipmentSlot second) {
        ItemStack firstItem = player.getItemBySlot(first);
        ItemStack secondItem = player.getItemBySlot(second);
        player.setItemSlot(first, secondItem);
        player.setItemSlot(second, firstItem);
        player.inventoryMenu.broadcastChanges();
    }

    public static <K, V> Map<K, V> mapTag(CompoundTag tag, Function<String, K> keyMapper, BiFunction<String, CompoundTag, V> valueMapper) {
        Map<K, V> map = new HashMap<>();
        for (String key : tag.getAllKeys()) {
            map.put(keyMapper.apply(key), valueMapper.apply(key, tag));
        }
        return map;
    }
}
