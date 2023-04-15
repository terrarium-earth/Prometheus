package earth.terrarium.prometheus.common.commands.admin;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import earth.terrarium.prometheus.common.handlers.role.RoleEntry;
import earth.terrarium.prometheus.common.handlers.role.RoleHandler;
import earth.terrarium.prometheus.common.menus.MemberRolesMenu;
import earth.terrarium.prometheus.common.menus.RolesMenu;
import earth.terrarium.prometheus.common.utils.ModUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class RolesCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("roles")
                .requires(source -> source.hasPermission(2))
                .executes(context -> {
                    openRolesMenu(context.getSource().getPlayerOrException());
                    return 1;
                }));
        dispatcher.register(Commands.literal("member")
                .requires(source -> source.hasPermission(2))
                .then(Commands.argument("player", GameProfileArgument.gameProfile())
                        .executes(context -> {
                            var player = GameProfileArgument.getGameProfiles(context, "player");
                            if (player.size() != 1) {
                                context.getSource().sendFailure(Component.translatable("prometheus.roles.member.error"));
                                return 0;
                            }
                            GameProfile profile = player.iterator().next();
                            MemberRolesMenu.open(context.getSource().getPlayerOrException(), profile);
                            return 1;
                        })));
    }

    public static void openRolesMenu(ServerPlayer player) {
        Set<UUID> editable = RoleHandler.getEditableRoles(player);
        List<RoleEntry> roles = RoleHandler.roles(player).roles();
        int starting = 0;
        for (RoleEntry role : roles) {
            if (editable.contains(role.id())) {
                starting = roles.indexOf(role);
                break;
            }
        }

        final int startingIndex = starting;

        ModUtils.openMenu(
                player,
                (i, inventory, playerx) -> new RolesMenu(i, roles, startingIndex),
                Component.translatable("prometheus.roles.title"),
                buf -> RolesMenu.write(buf, roles, startingIndex)
        );
    }
}
