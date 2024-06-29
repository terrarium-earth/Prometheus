package earth.terrarium.prometheus.common.handlers.permission.commands;

import com.mojang.brigadier.tree.CommandNode;
import earth.terrarium.prometheus.mixin.common.accessors.CommandNodeAccessor;
import net.minecraft.commands.CommandSourceStack;

import java.util.function.Predicate;

public class RequirementPropagation {

    public static void propagateRequirements(CommandNode<CommandSourceStack> node) {
        Predicate<CommandSourceStack> requirement = node.getRequirement();
        if (node.getCommand() == null) {
            setRequirement(node, context -> true);
            for (CommandNode<CommandSourceStack> child : node.getChildren()) {
                setRequirement(child, requirement.and(child.getRequirement()));
            }
        }
        for (CommandNode<CommandSourceStack> child : node.getChildren()) {
            propagateRequirements(child);
        }
    }

    @SuppressWarnings({"unchecked"})
    private static <T extends CommandSourceStack> void setRequirement(CommandNode<T> node, Predicate<T> requirement) {
        ((CommandNodeAccessor<T>) node).setRequirement(requirement);
    }
}
