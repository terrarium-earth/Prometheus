package earth.terrarium.prometheus.common.handlers.permission;

import earth.terrarium.prometheus.api.TriState;
import earth.terrarium.prometheus.api.permissions.PermissionApi;
import net.minecraft.world.entity.player.Player;

import java.util.*;
import java.util.function.Supplier;

public class PermissionApiImpl implements PermissionApi {

    private static final List<Supplier<List<String>>> AUTO_COMPLETE = new ArrayList<>();

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
}
