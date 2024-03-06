package earth.terrarium.prometheus.neoforge;

import com.teamresourceful.resourcefullib.common.utils.TriState;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.api.permissions.PermissionApi;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import net.neoforged.neoforge.server.permission.handler.IPermissionHandler;
import net.neoforged.neoforge.server.permission.nodes.PermissionDynamicContext;
import net.neoforged.neoforge.server.permission.nodes.PermissionNode;
import net.neoforged.neoforge.server.permission.nodes.PermissionTypes;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

public class PrometheusPermissionHandler implements IPermissionHandler {

    public static final ResourceLocation ID = new ResourceLocation(Prometheus.MOD_ID, "permissions");

    private final IPermissionHandler parent;
    private final Set<PermissionNode<?>> nodes;

    public PrometheusPermissionHandler(Collection<PermissionNode<?>> nodes) {
        this.parent = PrometheusNeoForge.getHandler(nodes);
        this.nodes = Set.copyOf(nodes);
    }

    @Override
    public @NotNull ResourceLocation getIdentifier() {
        return ID;
    }

    @Override
    public @NotNull Set<PermissionNode<?>> getRegisteredNodes() {
        return nodes;
    }

    @Override
    public <T> @NotNull T getPermission(@NotNull ServerPlayer player, @NotNull PermissionNode<T> node, PermissionDynamicContext<?> @NotNull ... contexts) {
        if (node.getType().equals(PermissionTypes.BOOLEAN)) {
            TriState state = PermissionApi.API.getPermission(player, node.getNodeName());
            if (state.isUndefined()) {
                return node.getDefaultResolver().resolve(player, player.getUUID(), contexts);
            }
            //noinspection unchecked
            return (T) Boolean.valueOf(state.isTrue());
        }
        if (parent != null) {
            return parent.getPermission(player, node, contexts);
        }
        return node.getDefaultResolver().resolve(player, player.getUUID(), contexts);
    }

    @Override
    public <T> @NotNull T getOfflinePermission(@NotNull UUID uuid, @NotNull PermissionNode<T> node, PermissionDynamicContext<?> @NotNull ... contexts) {
        if (node.getType().equals(PermissionTypes.BOOLEAN)) {
            TriState state = PermissionApi.API.getOfflinePermission(ServerLifecycleHooks.getCurrentServer(), uuid, node.getNodeName());
            if (state.isUndefined()) {
                return node.getDefaultResolver().resolve(null, uuid, contexts);
            }
            //noinspection unchecked
            return (T) Boolean.valueOf(state.isTrue());
        }
        if (parent != null) {
            return parent.getOfflinePermission(uuid, node, contexts);
        }
        return node.getDefaultResolver().resolve(null, uuid, contexts);
    }
}