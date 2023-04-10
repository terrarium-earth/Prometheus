package earth.terrarium.prometheus.api.roles.client;

import com.teamresourceful.resourcefullib.client.components.selection.ListEntry;
import earth.terrarium.prometheus.common.handlers.role.Role;

import java.util.List;

public interface OptionDisplay {

    List<ListEntry> getDisplayEntries();

    boolean save(Role role);
}
