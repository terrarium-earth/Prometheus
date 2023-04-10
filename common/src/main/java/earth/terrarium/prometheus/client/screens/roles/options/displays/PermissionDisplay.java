package earth.terrarium.prometheus.client.screens.roles.options.displays;

import com.teamresourceful.resourcefullib.client.components.selection.ListEntry;
import com.teamresourceful.resourcefullib.client.components.selection.SelectionList;
import earth.terrarium.prometheus.api.TriState;
import earth.terrarium.prometheus.api.roles.client.OptionDisplay;
import earth.terrarium.prometheus.client.screens.roles.options.entries.PermissionHeaderListEntry;
import earth.terrarium.prometheus.client.screens.roles.options.entries.PermissionListEntry;
import earth.terrarium.prometheus.common.handlers.role.Role;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record PermissionDisplay(List<ListEntry> entries, SelectionList<ListEntry> list) implements OptionDisplay {

    public static PermissionDisplay create(Role role, SelectionList<ListEntry> list) {
        List<ListEntry> entries = new ArrayList<>();
        entries.add(new PermissionHeaderListEntry(Component.literal("Permissions"), list, text -> {
            if (!text.isBlank()) {
                var entry = new PermissionListEntry(text, TriState.UNDEFINED, list, entries);
                list.addEntry(entry);
                entries.add(entry);
            }
        }));
        for (var entry : role.permissions().entrySet()) {
            entries.add(new PermissionListEntry(entry.getKey(), entry.getValue(), list, entries));
        }
        return new PermissionDisplay(entries, list);
    }

    @Override
    public List<ListEntry> getDisplayEntries() {
        return entries;
    }

    @Override
    public boolean save(Role role) {
        Map<String, TriState> perms = new HashMap<>();
        for (var entry : entries) {
            if (entry instanceof PermissionListEntry permEntry) {
                perms.put(permEntry.permission(), permEntry.state());
            }
        }
        role.setPermissions(perms);
        return true;
    }
}
