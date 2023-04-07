package earth.terrarium.prometheus.common.commands.testing;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.serialization.JsonOps;
import com.teamresourceful.resourcefullib.common.color.Color;
import earth.terrarium.prometheus.api.TriState;
import earth.terrarium.prometheus.common.handlers.permission.PermissionHolder;
import earth.terrarium.prometheus.common.handlers.role.Role;
import earth.terrarium.prometheus.common.handlers.role.RoleHandler;
import earth.terrarium.prometheus.common.handlers.role.options.defaults.DisplayOption;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.network.chat.Component;

public class PermissionCommand {

    private static final SuggestionProvider<CommandSourceStack> SUGGEST_IDS = (context, builder) -> {
        SharedSuggestionProvider.suggest(
                RoleHandler.getRoles(context.getSource().getPlayer()).getIdentifiers(),
                builder, Object::toString, id -> Component.literal(id.toString()));
        return builder.buildFuture();
    };

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("permissiontest").executes(context -> {
            if (context.getSource().getPlayerOrException() instanceof PermissionHolder holder) {
                holder.prometheus$updatePermissions();
                System.out.println(holder.prometheus$getPermissions());
            }
            return 1;
        }));
        dispatcher.register(Commands.literal("addrole").then(
                Commands.argument("id", StringArgumentType.string())
                        .then(Commands.argument("state", StringArgumentType.word())
                                .executes(context -> {
                                    String id = StringArgumentType.getString(context, "id");
                                    String state = StringArgumentType.getString(context, "state");
                                    Role role = new Role();
                                    role.setPermission(id, state.equals("true") ? TriState.TRUE : state.equals("false") ? TriState.FALSE : TriState.UNDEFINED);
                                    System.out.println(RoleHandler.addRole(context.getSource().getPlayerOrException(), role));
                                    return 1;
                                })
                        )
        ));
        dispatcher.register(Commands.literal("getoption").then(
                Commands.argument("id", UuidArgument.uuid()).suggests(SUGGEST_IDS)
                        .executes(context -> {
                            DisplayOption option = RoleHandler.getRoles(context.getSource().getPlayerOrException())
                                    .getRole(UuidArgument.getUuid(context, "id"))
                                    .getData(DisplayOption.SERIALIZER);
                            if (option == null) {
                                System.out.println("null");
                                return 1;
                            }
                            DisplayOption.SERIALIZER.codec().encodeStart(JsonOps.INSTANCE, option)
                                    .result().ifPresent(System.out::println);
                            return 1;
                        })
        ));
        dispatcher.register(Commands.literal("setoption").then(
                Commands.argument("id", UuidArgument.uuid()).suggests(SUGGEST_IDS)
                        .executes(context -> {
                            RoleHandler.getRoles(context.getSource().getPlayerOrException())
                                    .getRole(UuidArgument.getUuid(context, "id"))
                                    .setData(new DisplayOption(Component.literal("Test"), Color.RAINBOW));
                            return 1;
                        })
        ));
    }
}
