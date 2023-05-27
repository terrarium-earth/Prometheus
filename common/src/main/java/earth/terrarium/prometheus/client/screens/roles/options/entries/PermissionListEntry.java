package earth.terrarium.prometheus.client.screens.roles.options.entries;

import com.teamresourceful.resourcefullib.client.components.selection.ListEntry;
import com.teamresourceful.resourcefullib.client.components.selection.SelectionList;
import com.teamresourceful.resourcefullib.common.utils.TriState;
import net.minecraft.network.chat.Component;

import java.util.List;

public class PermissionListEntry extends TriStateListEntry {

    private final String permission;

    public PermissionListEntry(String id, TriState state, SelectionList<ListEntry> list, List<ListEntry> list2) {
        super(Component.translatableWithFallback(id + "." + "permission", id), state, entry -> {
            list.removeEntry(entry);
            list2.remove(entry);
        });
        this.permission = id;
    }

    public String permission() {
        return permission;
    }
}
