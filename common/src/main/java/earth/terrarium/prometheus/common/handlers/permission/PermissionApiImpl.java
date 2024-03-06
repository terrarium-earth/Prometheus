package earth.terrarium.prometheus.common.handlers.permission;

import com.teamresourceful.resourcefullib.common.utils.TriState;
import earth.terrarium.prometheus.api.permissions.PermissionApi;
import earth.terrarium.prometheus.common.handlers.role.RoleHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;

import java.util.*;
import java.util.function.Supplier;

public class PermissionApiImpl implements PermissionApi {

    private static final List<Supplier<List<String>>> AUTO_COMPLETE = new ArrayList<>();
    private static final Map<String, TriState> DEFAULT_PERMISSIONS = new HashMap<>();

    @Override
    public TriState getPermission(Player player, String permission) {
        if (player instanceof PermissionHolder holder) {
            if (holder.prometheus$getPermissions() == null) {
                holder.prometheus$updatePermissions();
            }
            return holder.prometheus$hasPermission(permission);
        }
        return TriState.UNDEFINED;
    }

    @Override
    public TriState getOfflinePermission(MinecraftServer server, UUID id, String permission) {
        var permissions = RoleHandler.getOfflinePermissions(server.overworld(), id);
        return permissions.getOrDefault(permission, TriState.UNDEFINED);
    }

    @Override
    public void addAutoComplete(Supplier<List<String>> permission) {
        AUTO_COMPLETE.add(permission);
    }

    @Override
    public List<String> getAutoComplete(String text, Set<String> permissions) {
        if (text == null || text.isBlank()) return List.of();
        Set<String> complete = new LinkedHashSet<>();
        AUTO_COMPLETE.stream()
            .map(Supplier::get)
            .flatMap(List::stream)
            .filter(s -> !permissions.contains(s))
            .filter(s -> s.startsWith(text))
            .map(s -> s.substring(text.length()))
            .map(s -> s.split("\\.")[0])
            .forEach(complete::add);
        return new ArrayList<>(complete);
    }

    @Override
    public List<String> getAutoComplete(Set<String> permissions) {
        Set<String> complete = new LinkedHashSet<>();
        AUTO_COMPLETE.stream()
            .map(Supplier::get)
            .flatMap(List::stream)
            .filter(s -> !permissions.contains(s))
            .map(s -> s.split("\\.")[0])
            .forEach(complete::add);
        return new ArrayList<>(complete);
    }

    @Override
    public List<String> getAutoComplete() {
        Set<String> complete = new LinkedHashSet<>();
        AUTO_COMPLETE.stream()
            .map(Supplier::get)
            .flatMap(List::stream)
            .forEach(complete::add);
        return new ArrayList<>(complete);
    }

    @Override
    public void addDefaultPermission(String permission, TriState state) {
        DEFAULT_PERMISSIONS.put(permission, state);
    }

    @Override
    public Map<String, TriState> getDefaultPermissions() {
        return DEFAULT_PERMISSIONS;
    }
}
