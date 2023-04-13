package earth.terrarium.prometheus.common.handlers.permission;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;
import earth.terrarium.prometheus.api.TriState;
import earth.terrarium.prometheus.api.permissions.PermissionApi;
import earth.terrarium.prometheus.mixin.common.CommandNodeAccessor;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class CommandPermissionHandler {

    public static final List<String> COMMAND_PERMS = new ArrayList<>();

    public static void registerPermissions(CommandDispatcher<CommandSourceStack> dispatcher) {
        Map<CommandNode<CommandSourceStack>, String> cache = new Object2ObjectOpenHashMap<>();
        modifyCommandNode(dispatcher, dispatcher.getRoot(), "commands", cache);
        COMMAND_PERMS.clear();
        COMMAND_PERMS.addAll(cache.values());
    }

    private static void modifyCommandNode(CommandDispatcher<CommandSourceStack> dispatcher, CommandNode<CommandSourceStack> node, String prefix, Map<CommandNode<CommandSourceStack>, String> cache) {
        for (var child : node.getChildren()) {
            if (!child.isFork()) {
                String childPermission = prefix + "." + child.getName();
                cache.put(child, childPermission);
                CommandNode<CommandSourceStack> redirect = child.getRedirect();
                if (redirect != null && redirect != dispatcher.getRoot()) {
                    setRequirement(child, new RedirectedPermissionPredicate(() -> cache.get(redirect), new PermissionPredicate(childPermission, child.getRequirement())));
                } else {
                    setRequirement(child, new PermissionPredicate(childPermission, child.getRequirement()));
                }

                modifyCommandNode(dispatcher, child, childPermission, cache);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends CommandSourceStack> void setRequirement(CommandNode<T> node, Predicate<T> requirement) {
        CommandNodeAccessor<T> accessor = (CommandNodeAccessor<T>) node;
        accessor.setRequirement(requirement);
    }

    private record PermissionPredicate(String permission, Predicate<CommandSourceStack> original) implements Predicate<CommandSourceStack> {

        @Override
        public boolean test(CommandSourceStack stack) {
            if (stack.isPlayer()) {
                TriState state = PermissionApi.API.getPermission(stack.getPlayer(), permission);
                if (state.isUndefined()) {
                    state = recursivePermissionCheck(stack.getPlayer(), permission.substring(0, permission.lastIndexOf('.')));
                }
                return (state.isUndefined() && original.test(stack)) || state.isTrue();
            }
            return original.test(stack);
        }
    }

    private static TriState recursivePermissionCheck(ServerPlayer player, String permission) {
        if (permission.equals("commands") || permission.isEmpty()) {
            return TriState.UNDEFINED;
        }
        TriState state = PermissionApi.API.getPermission(player, permission + ".*");
        if (state.isUndefined()) {
            return recursivePermissionCheck(player, permission.substring(0, permission.lastIndexOf('.')));
        }
        return state;
    }

    private record RedirectedPermissionPredicate(Supplier<String> deferredPermission, PermissionPredicate predicate) implements Predicate<CommandSourceStack> {

        @Override
        public boolean test(CommandSourceStack stack) {
            String permission = deferredPermission().get();
            if (permission == null) {
                permission = predicate().permission();
            }
            if (stack.isPlayer()) {
                TriState state = PermissionApi.API.getPermission(stack.getPlayer(), permission);
                return (state.isUndefined() && predicate().original().test(stack)) || state.isTrue();
            }
            return predicate().original().test(stack);
        }
    }
}
