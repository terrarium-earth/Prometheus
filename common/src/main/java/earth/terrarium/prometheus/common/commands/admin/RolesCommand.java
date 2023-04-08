package earth.terrarium.prometheus.common.commands.admin;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.datafixers.util.Pair;
import earth.terrarium.prometheus.common.handlers.role.Role;
import earth.terrarium.prometheus.common.handlers.role.RoleHandler;
import earth.terrarium.prometheus.common.menus.RolesMenu;
import earth.terrarium.prometheus.common.utils.ModUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.UUID;

public class RolesCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("roles")
                .requires(source -> source.hasPermission(2))
                .executes(context -> {
                    openRolesMenu(context.getSource().getPlayerOrException());
                    return 1;
                }));
    }

    public static void openRolesMenu(ServerPlayer player) {
        List<Pair<UUID, Role>> roles = RoleHandler.getRoles(player).getRoles();

        ModUtils.openMenu(
                player,
                (i, inventory, playerx) -> new RolesMenu(i, roles),
                Component.translatable("prometheus.roles.title"),
                buf -> RolesMenu.write(buf, roles)
        );
    }
}
