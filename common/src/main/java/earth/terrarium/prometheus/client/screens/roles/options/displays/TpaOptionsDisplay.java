package earth.terrarium.prometheus.client.screens.roles.options.displays;

import com.teamresourceful.resourcefullib.client.components.selection.ListEntry;
import com.teamresourceful.resourcefullib.client.components.selection.SelectionList;
import earth.terrarium.prometheus.api.roles.client.OptionDisplay;
import earth.terrarium.prometheus.client.screens.roles.options.entries.NumberBoxListEntry;
import earth.terrarium.prometheus.client.screens.roles.options.entries.TextListEntry;
import earth.terrarium.prometheus.common.constants.ConstantComponents;
import earth.terrarium.prometheus.common.handlers.role.Role;
import earth.terrarium.prometheus.common.handlers.role.options.defaults.TpaOptions;

import java.util.List;

public record TpaOptionsDisplay(List<ListEntry> entries) implements OptionDisplay {

    public static TpaOptionsDisplay create(Role role, SelectionList<ListEntry> ignored) {
        TpaOptions tpa = role.getNonNullOption(TpaOptions.SERIALIZER);
        return new TpaOptionsDisplay(List.of(
                new TextListEntry(ConstantComponents.TPA_TITLE),
                new NumberBoxListEntry(tpa.expire(), false, ConstantComponents.REQUEST_TIMEOUT)
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
                role.setData(new TpaOptions(intValue.getAsInt()));
                return true;
            }
        }
        return false;
    }
}
