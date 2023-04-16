package earth.terrarium.prometheus.client.screens.roles.options.displays;

import com.teamresourceful.resourcefullib.client.components.selection.ListEntry;
import com.teamresourceful.resourcefullib.client.components.selection.SelectionList;
import earth.terrarium.prometheus.api.roles.client.OptionDisplay;
import earth.terrarium.prometheus.client.screens.roles.options.entries.NumberBoxListEntry;
import earth.terrarium.prometheus.client.screens.roles.options.entries.TextListEntry;
import earth.terrarium.prometheus.common.constants.ConstantComponents;
import earth.terrarium.prometheus.common.handlers.role.Role;
import earth.terrarium.prometheus.common.roles.TeleportOptions;

import java.util.List;

public record TeleportOptionsDisplay(List<ListEntry> entries) implements OptionDisplay {

    public static TeleportOptionsDisplay create(Role role, SelectionList<ListEntry> ignored) {
        TeleportOptions tpa = role.getNonNullOption(TeleportOptions.SERIALIZER);
        return new TeleportOptionsDisplay(List.of(
            new TextListEntry(ConstantComponents.TELEPORT_TITLE),
            new NumberBoxListEntry(tpa.expire(), false, ConstantComponents.REQUEST_TIMEOUT),
            new NumberBoxListEntry(tpa.rtpCooldown(), false, ConstantComponents.RTP_COOLDOWN),
            new NumberBoxListEntry(tpa.rtpDistance(), false, ConstantComponents.RTP_DISTANCE)
        ));
    }

    @Override
    public List<ListEntry> getDisplayEntries() {
        return entries;
    }

    @Override
    public boolean save(Role role) {
        NumberBoxListEntry expire = (NumberBoxListEntry) entries.get(1);
        NumberBoxListEntry rtpCooldown = (NumberBoxListEntry) entries.get(2);
        NumberBoxListEntry rtpDistance = (NumberBoxListEntry) entries.get(2);
        if (expire.getIntValue().isPresent() && rtpCooldown.getIntValue().isPresent() && rtpDistance.getIntValue().isPresent()) {
            role.setData(new TeleportOptions(expire.getIntValue().getAsInt(), rtpCooldown.getIntValue().getAsInt(), rtpDistance.getIntValue().getAsInt()));
            return true;
        }
        return false;
    }
}
