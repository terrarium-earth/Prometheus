package earth.terrarium.prometheus.client.screens.roles.options.displays;

import com.teamresourceful.resourcefullib.client.components.selection.ListEntry;
import com.teamresourceful.resourcefullib.client.components.selection.SelectionList;
import earth.terrarium.prometheus.api.roles.client.OptionDisplay;
import earth.terrarium.prometheus.client.screens.roles.options.entries.TextBoxListEntry;
import earth.terrarium.prometheus.client.screens.roles.options.entries.TextListEntry;
import earth.terrarium.prometheus.common.constants.ConstantComponents;
import earth.terrarium.prometheus.common.handlers.role.Role;
import earth.terrarium.prometheus.common.handlers.role.options.defaults.CosmeticOptions;

import java.util.List;

public record CosmeticOptionsDisplay(List<ListEntry> entries) implements OptionDisplay {

    public static CosmeticOptionsDisplay create(Role role, SelectionList<ListEntry> ignored) {
        CosmeticOptions display = role.getNonNullOption(CosmeticOptions.SERIALIZER);
        List<ListEntry> entries = List.of(
                new TextListEntry(ConstantComponents.COSMETIC_TITLE),
                new TextBoxListEntry(display.display(), 24, ConstantComponents.COSMETIC_ROLE_NAME, text -> !text.isBlank()),
                new TextBoxListEntry(display.icon(), 1, ConstantComponents.COSMETIC_ROLE_ICON, text -> text.codePoints().count() == 1 && !text.isBlank())
        );
        return new CosmeticOptionsDisplay(entries);
    }

    @Override
    public List<ListEntry> getDisplayEntries() {
        return entries;
    }

    @Override
    public boolean save(Role role) {
        CosmeticOptions display = role.getNonNullOption(CosmeticOptions.SERIALIZER);
        CosmeticOptions newDisplay = new CosmeticOptions(
                ((TextBoxListEntry) entries.get(1)).getText(),
                ((TextBoxListEntry) entries.get(2)).getText(),
                display.color()
        );
        if (!newDisplay.display().isBlank() && !newDisplay.icon().isBlank()) {
            role.setData(newDisplay);
            return true;
        }
        return false;
    }
}
