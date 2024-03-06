package earth.terrarium.prometheus.client.ui.roles.editing.pages.permissions;

import com.teamresourceful.resourcefullib.common.utils.TriState;
import earth.terrarium.olympus.client.components.lists.EntryListWidget;
import earth.terrarium.prometheus.common.handlers.role.Role;
import it.unimi.dsi.fastutil.objects.ObjectObjectMutablePair;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PermissionList extends EntryListWidget<ObjectObjectMutablePair<String, TriState>> {

    private final Role role;
    private final Runnable saver;

    public PermissionList(@Nullable EntryListWidget<ObjectObjectMutablePair<String, TriState>> list, int width, int height, Role role, Runnable saver) {
        super(list, width, height);
        this.role = role;
        this.saver = saver;
    }

    @Override
    public void update() {
        clear();
        List<ObjectObjectMutablePair<String, TriState>> permissions = this.role.permissions()
            .entrySet()
            .stream()
            .map(entry -> ObjectObjectMutablePair.of(entry.getKey(), entry.getValue()))
            .sorted((a, b) -> {
                String id = a.left().split("\\.")[0];
                String id2 = b.left().split("\\.")[0];
                return id.compareTo(id2);
            })
            .toList();


        for (var permission : permissions) {
            PermissionListEntry entry = new PermissionListEntry(
                permission,
                this.role.permissions(),
                this.saver
            );
            this.add(entry);
        }
    }
}
