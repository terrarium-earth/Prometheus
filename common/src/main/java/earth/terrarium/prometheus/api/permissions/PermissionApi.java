package earth.terrarium.prometheus.api.permissions;

import com.teamresourceful.resourcefullib.common.utils.TriState;
import earth.terrarium.prometheus.api.ApiHelper;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public interface PermissionApi {

    PermissionApi API = ApiHelper.load(PermissionApi.class);

    /**
     * Checks if a player has a permission
     *
     * @param player     the player to check
     * @param permission the permission to check
     * @return the result of the check
     */
    TriState getPermission(Player player, String permission);

    /**
     * Adds a permission to the auto complete list
     *
     * @param permission the permission to add
     */
    default void addAutoComplete(String permission) {
        List<String> list = List.of(permission);
        addAutoComplete(() -> list);
    }

    /**
     * Adds a permission to the auto complete list
     *
     * @param permission the permission to add
     */
    void addAutoComplete(Supplier<List<String>> permission);

    /**
     * Gets the list of possible auto complete options
     *
     * @param text        the text to auto complete
     * @param permissions the permissions already found and should not be added
     * @return the auto complete list
     */
    List<String> getAutoComplete(String text, Set<String> permissions);

    /**
     * Gets the list of possible auto complete options
     *
     * @param permissions the permissions already found and should not be added
     * @return the auto complete list
     */
    default List<String> getAutoComplete(Set<String> permissions) {
        return List.of();
    }

    /**
     * Adds a default permission to the map
     *
     * @param permission the permission to add
     * @param state the default state of the permission
     */
    void addDefaultPermission(String permission, TriState state);

    /**
     * Gets all default permissions
     *
     * @return the default permissions
     */
    Map<String, TriState> getDefaultPermissions();
}
