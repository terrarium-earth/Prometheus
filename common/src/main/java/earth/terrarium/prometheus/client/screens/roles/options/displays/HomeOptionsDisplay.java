package earth.terrarium.prometheus.client.screens.roles.options.displays;

import com.teamresourceful.resourcefullib.client.components.selection.ListEntry;
import com.teamresourceful.resourcefullib.client.components.selection.SelectionList;
import earth.terrarium.prometheus.api.roles.client.OptionDisplay;
import earth.terrarium.prometheus.client.screens.roles.options.entries.NumberBoxListEntry;
import earth.terrarium.prometheus.client.screens.roles.options.entries.TextListEntry;
import earth.terrarium.prometheus.common.constants.ConstantComponents;
import earth.terrarium.prometheus.common.handlers.role.Role;
import earth.terrarium.prometheus.common.roles.HomeOptions;

import java.util.List;

public record HomeOptionsDisplay(List<ListEntry> entries) implements OptionDisplay {

    public static HomeOptionsDisplay create(Role role, SelectionList<ListEntry> ignored) {
        HomeOptions home = role.getNonNullOption(HomeOptions.SERIALIZER);
        return new HomeOptionsDisplay(List.of(
                new TextListEntry(ConstantComponents.HOMES_TITLE),
                new NumberBoxListEntry(home.max(), false, ConstantComponents.HOMES_MAX)
        ));
    }

    @Override
    public List<ListEntry> getDisplayEntries() {
        return entries;
    }

    @Override
    public boolean save(Role role) {
        if (entries.get(1) instanceof NumberBoxListEntry box) {
            var intValue = box.getIntValue();
            if (intValue.isPresent()) {
                role.setData(new HomeOptions(intValue.getAsInt()));
                return true;
            }
        }
        return false;
    }
}
