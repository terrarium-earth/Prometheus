package earth.terrarium.prometheus.common.handlers.permission.commands;

import com.mojang.brigadier.tree.CommandNode;
import earth.terrarium.prometheus.common.utils.NodeUtils;
import net.minecraft.commands.CommandSourceStack;

import java.util.function.Predicate;

public class RequirementPropagation {

    public static void propagateRequirements(CommandNode<CommandSourceStack> node) {
        Predicate<CommandSourceStack> requirement = node.getRequirement();
        if (node.getCommand() == null) {
            NodeUtils.setRequirement(node, context -> true);
            for (CommandNode<CommandSourceStack> child : node.getChildren()) {
                NodeUtils.setRequirement(child, requirement.and(child.getRequirement()));
            }
        }
        for (CommandNode<CommandSourceStack> child : node.getChildren()) {
            propagateRequirements(child);
        }
    }
}
