package earth.terrarium.prometheus.common.handlers.permission.commands;

import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.commands.CommandSourceStack;

public class NodeMarker {

    public static void markCommandNodes(CommandNode<CommandSourceStack> root) {
        setPermission(root, "commands");
        root.getChildren().forEach(child -> markPermissions(child, "commands"));
    }

    private static void markPermissions(CommandNode<CommandSourceStack> node, String prefix) {
        String permission = prefix + "." + node.getName();
        setPermission(node, permission);
        node.getChildren().forEach(child -> markPermissions(child, permission));
    }

    private static void setPermission(CommandNode<CommandSourceStack> node, String permission) {
        ((CommandNodeExtension) node).prometheus$setPermission(permission);
    }
}
