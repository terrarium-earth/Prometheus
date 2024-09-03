package earth.terrarium.prometheus.common.handlers.permission.commands;

import com.mojang.brigadier.tree.CommandNode;
import com.teamresourceful.resourcefullib.common.utils.TriState;
import earth.terrarium.prometheus.api.permissions.PermissionApi;
import earth.terrarium.prometheus.common.utils.NodeUtils;
import earth.terrarium.prometheus.mixin.common.accessors.CommandNodeAccessor;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class NamedPermissionNodes {

    public static void modifyPermissions(CommandNode<CommandSourceStack> node) {
        String permission = NodeUtils.getPermission(node);
        if (permission == null) return;
        Predicate<CommandSourceStack> original = node.getRequirement();
        Predicate<CommandSourceStack> modified = createPermissionPredicate(permission, original);
        setRequirement(node, modified);

        for (CommandNode<CommandSourceStack> child : node.getChildren()) {
            modifyPermissions(child);
        }
    }

    @SuppressWarnings({"unchecked"})
    private static <T extends CommandSourceStack> void setRequirement(CommandNode<T> node, Predicate<T> requirement) {
        ((CommandNodeAccessor<T>) node).setRequirement(requirement);
    }

    private static Predicate<CommandSourceStack> createPermissionPredicate(String commandPermission, Predicate<CommandSourceStack> original) {
        List<String> permissions = generatePermissions(commandPermission);
        return source -> {
            if (source.isPlayer()) {
                ServerPlayer player = Objects.requireNonNull(source.getPlayer());
                for (String permission : permissions) {
                    TriState state = PermissionApi.API.getPermission(player, permission);
                    if (state.isDefined()) return state.isTrue();
                }
            }
            return original.test(source);
        };
    }

    private static List<String> generatePermissions(String permission) {
        List<String> permissions = new ArrayList<>();
        permissions.add(permission);
        permissions.add("%s.*".formatted(permission));
        while (permission.contains(".")) {
            permission = permission.substring(0, permission.lastIndexOf('.'));
            permissions.add("%s.*".formatted(permission));
        }
        permissions.add("*");
        return permissions;
    }
}
