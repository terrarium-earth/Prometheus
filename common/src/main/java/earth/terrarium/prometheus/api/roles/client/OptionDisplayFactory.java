package earth.terrarium.prometheus.api.roles.client;

import com.teamresourceful.resourcefullib.client.components.selection.ListEntry;
import com.teamresourceful.resourcefullib.client.components.selection.SelectionList;
import earth.terrarium.prometheus.common.handlers.role.Role;

@FunctionalInterface
public interface OptionDisplayFactory {

    OptionDisplay create(Role role, SelectionList<ListEntry> list);
}
