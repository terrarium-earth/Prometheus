package earth.terrarium.prometheus.common.utils;

import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Predicate;

public class NodeUtils {
    public static IdentityHashMap<CommandNode<?>, String> permissions = new IdentityHashMap<>();

    public static void setPermission(CommandNode<?> node, String permission) {
        permissions.put(node, permission);
    }

    public static String getPermission(CommandNode<?> node) {
        return permissions.get(node);
    }

    @SuppressWarnings("unchecked")
    public static <T> void removeChild(CommandNode<T> parent, CommandNode<?> node) {
        try {
            var childrenField = CommandNode.class.getDeclaredField("children");
            childrenField.setAccessible(true);
            var literalsField = CommandNode.class.getDeclaredField("literals");
            literalsField.setAccessible(true);
            var argumentsField = CommandNode.class.getDeclaredField("arguments");
            argumentsField.setAccessible(true);

            var children = ((Map<String, CommandNode<T>>) childrenField.get(parent));
            var literals = ((Map<String, LiteralCommandNode<T>>) literalsField.get(parent));
            var arguments = ((Map<String, ArgumentCommandNode<T, ?>>) argumentsField.get(parent));

            children.remove(node.getName());
            literals.remove(node.getName());
            arguments.remove(node.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setRequirement(CommandNode<?> node, Predicate<?> requirement) {
        try {
            var requirementField = CommandNode.class.getDeclaredField("requirement");
            requirementField.setAccessible(true);
            requirementField.set(node, requirement);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
