package earth.terrarium.prometheus.common.handlers.permission;

import earth.terrarium.prometheus.api.TriState;
import earth.terrarium.prometheus.api.permissions.PermissionApi;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

public class PermissionApiImpl implements PermissionApi {

    private static final List<String> AUTO_COMPLETE = new ArrayList<>();

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
    public void addAutoComplete(String permission) {
        AUTO_COMPLETE.add(permission);
    }

    @Override
    public List<String> getAutoComplete(String text) {
        return text.isBlank() ? List.of() : AUTO_COMPLETE.stream()
                .filter(s -> s.startsWith(text))
                .map(s -> s.substring(text.length()))
                .map(s -> s.split("\\.")[0])
                .toList();
    }
}
