package earth.terrarium.prometheus.common.commands.roles;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import earth.terrarium.prometheus.common.handlers.role.RoleEntry;
import earth.terrarium.prometheus.common.handlers.role.RoleHandler;
import earth.terrarium.prometheus.common.menus.content.RolesContent;
import earth.terrarium.prometheus.common.network.NetworkHandler;
import earth.terrarium.prometheus.common.network.messages.client.screens.OpenRolesScreenPacket;
import earth.terrarium.prometheus.common.roles.CosmeticOptions;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class RolesCommand {

    public static final SuggestionProvider<CommandSourceStack> SUGGEST_ROLES = (context, builder) -> {
        Iterable<RoleEntry> roles = RoleHandler.roles(context.getSource().getLevel());
        SharedSuggestionProvider.suggest(roles, builder,
            entry -> entry.id().toString(),
            entry -> Component.literal(entry.role().getNonNullOption(CosmeticOptions.SERIALIZER).display()));
        return builder.buildFuture();
    };

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("roles")
            .requires(source -> source.hasPermission(2))
            .then(Commands.literal("open").executes(context -> {
                var player = context.getSource().getPlayerOrException();
                Set<UUID> editable = RoleHandler.getEditableRoles(player);
                List<RoleEntry> roles = RoleHandler.roles(player.level()).roles();
                for (RoleEntry role : roles) {
                    if (editable.contains(role.id())) {
                        NetworkHandler.CHANNEL.sendToPlayer(new OpenRolesScreenPacket(new RolesContent(roles, roles.indexOf(role))), player);
                        return 1;
                    }
                }
                return 1;
            }))
            .executes(context -> {
                var source = context.getSource();
                source.sendSystemMessage(Component.literal("Roles:"));
                for (RoleEntry role : RoleHandler.roles(source.getLevel())) {
                    source.sendSystemMessage(
                        Component.literal(role.id().toString() +
                            " - " +
                            role.role().getNonNullOption(CosmeticOptions.SERIALIZER).display())
                    );
                }
                return 1;
            })
        );
    }
}
