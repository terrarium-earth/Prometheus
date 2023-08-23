package earth.terrarium.prometheus.common.commands.admin;

import com.mojang.brigadier.CommandDispatcher;
import earth.terrarium.prometheus.common.menus.InvseeMenu;
import earth.terrarium.prometheus.common.menus.WrappedPlayerContainer;
import earth.terrarium.prometheus.common.network.NetworkHandler;
import earth.terrarium.prometheus.common.network.messages.client.screens.OpenInvseeScreenPacket;
import earth.terrarium.prometheus.mixin.common.accessors.ServerPlayerAccessor;
import earth.terrarium.prometheus.mixin.common.accessors.ServerPlayerInvoker;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;

public class InvseeCommand {

    private static final Component TITLE = Component.translatable("container.enderchest");

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("enderchest")
            .requires(source -> source.hasPermission(2))
            .then(Commands.argument("player", EntityArgument.player())
                .executes(context -> {
                    InvseeCommand.openEnderChest(context.getSource().getEntity(), EntityArgument.getPlayer(context, "player"));
                    return 1;
                })
            ).executes(context -> {
                InvseeCommand.openEnderChest(context.getSource().getEntity(), context.getSource().getEntity());
                return 1;
            }));

        dispatcher.register(Commands.literal("invsee")
            .requires(source -> source.hasPermission(2))
            .then(Commands.argument("player", EntityArgument.player())
                .executes(context -> {
                    ServerPlayer opener = context.getSource().getPlayerOrException();
                    if (!NetworkHandler.CHANNEL.canSendPlayerPackets(opener)) {
                        context.getSource().sendFailure(Component.literal("You cant use invsee unless you have the client installed"));
                        return 0;
                    }

                    ServerPlayer player = EntityArgument.getPlayer(context, "player");

                    if (opener.containerMenu != opener.inventoryMenu) {
                        opener.closeContainer();
                    }


                    ((ServerPlayerInvoker)opener).invokeNextContainerCounter();
                    int counter = ((ServerPlayerAccessor)opener).getContainerCounter();

                    NetworkHandler.CHANNEL.sendToPlayer(
                        new OpenInvseeScreenPacket(
                            counter,
                            player.getInventory().getContainerSize(),
                            player.getUUID(),
                            Component.translatable("prometheus.invsee.inventory", player.getDisplayName())
                        ),
                        opener
                    );
                    opener.containerMenu = new InvseeMenu(
                        counter,
                        opener.getInventory(),
                        player,
                        new WrappedPlayerContainer(player),
                        player.getUUID()
                    );
                    ((ServerPlayerInvoker)opener).invokeInitMenu(opener.containerMenu);
                    return 1;
                })
            ));
    }

    public static void openEnderChest(Entity opener, Entity target) {
        if (opener instanceof ServerPlayer playerOpener && target instanceof Player playerTarget) {
            var title = Component.translatable("prometheus.players.inventory", playerTarget.getDisplayName(), TITLE);

            playerOpener.openMenu(new SimpleMenuProvider(
                (i, inventory, playerx) -> ChestMenu.threeRows(i, inventory, playerTarget.getEnderChestInventory()),
                title
            ));
        }
    }
}
