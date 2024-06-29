package earth.terrarium.prometheus.common.handlers.permission;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import earth.terrarium.prometheus.common.handlers.permission.commands.*;
import earth.terrarium.prometheus.common.network.NetworkHandler;
import earth.terrarium.prometheus.common.network.messages.client.ClientboundCommandPermissionsPacket;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;

/**
 * This permission handling is done in 4 steps:
 * <br>
 * - Step 1 give all node its own permission id.
 * <br>
 * - Step 2 go through all nodes and if a node does not have an execution and
 * only has children but has a requirement propagate the requirement to the children.
 * <br>
 * - Step 3 go through all nodes and set the permission predicate to the node with a fallback to the OG modified one if
 * permission is undefined.
 * <p>
 * - Step 4 when the commands are sent to the client clean up nodes which do not execute and have no children.
 * Must work backwards from the leaf nodes to the root node as there can be a chain of children ie. data -> get -> block
 */
public class CommandPermissions {

    private static final List<String> commandPermissions = new ArrayList<>();

    public static void registerPermissions(CommandDispatcher<CommandSourceStack> dispatcher) {
        NodeMarker.markCommandNodes(dispatcher.getRoot());
        RequirementPropagation.propagateRequirements(dispatcher.getRoot());
        NamedPermissionNodes.modifyPermissions(dispatcher.getRoot());
        collectPermissions(dispatcher.getRoot());
    }

    public static RootCommandNode<SharedSuggestionProvider> sendCommands(RootCommandNode<SharedSuggestionProvider> root) {
        return CommandTreeCleaner.cleanTree(root);
    }

    public static void sendCommandPermissions(ServerPlayer player) {
        NetworkHandler.CHANNEL.sendToPlayer(new ClientboundCommandPermissionsPacket(commandPermissions), player);
    }

    private static void collectPermissions(CommandNode<CommandSourceStack> root) {
        CommandNodeExtension extension = (CommandNodeExtension) root;
        commandPermissions.add(extension.prometheus$getPermission());
        for (CommandNode<CommandSourceStack> child : root.getChildren()) {
            collectPermissions(child);
        }
    }

    public static void setCommandPermissions(List<String> permissions) {
        commandPermissions.clear();
        commandPermissions.addAll(permissions);
    }

    public static List<String> getCommandPermissions() {
        return commandPermissions;
    }
}
