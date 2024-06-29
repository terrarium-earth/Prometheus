package earth.terrarium.prometheus.common.handlers.permission.commands;

import com.mojang.brigadier.tree.CommandNode;

import java.util.ArrayList;
import java.util.List;

public class CommandTreeCleaner {

    public static <S, T extends CommandNode<S>> T cleanTree(T root) {
        CommandNodeExtension extension = (CommandNodeExtension) root;
        List<CommandNode<?>> children = new ArrayList<>(root.getChildren());
        for (CommandNode<?> child : children) {
            if (canRemove(child)) {
                extension.prometheus$removeChild(child);
            } else {
                cleanTree(child);
            }
        }
        return root;
    }

    private static boolean canRemove(CommandNode<?> node) {
        if (node.getCommand() != null) return false;
        if (node.getChildren().isEmpty()) return true;
        for (CommandNode<?> child : node.getChildren()) {
            if (!canRemove(child)) return false;
        }
        return true;
    }
}
