package earth.terrarium.prometheus.common.utils.forge;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.MenuConstructor;
import net.minecraftforge.network.NetworkHooks;

import java.util.function.Consumer;

public class ModUtilsImpl {
    public static void openMenu(ServerPlayer player, MenuConstructor constructor, Component title, Consumer<FriendlyByteBuf> options) {
        NetworkHooks.openScreen(player, new SimpleMenuProvider(
            constructor,
            title
        ), options);
    }
}
