package earth.terrarium.prometheus.client.screens.roles.editing;

import com.teamresourceful.resourcefullib.client.components.selection.ListEntry;
import com.teamresourceful.resourcefullib.client.components.selection.SelectionList;
import earth.terrarium.prometheus.api.roles.client.OptionDisplay;
import earth.terrarium.prometheus.api.roles.client.OptionDisplayApi;
import earth.terrarium.prometheus.common.handlers.role.Role;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class OptionDisplayList extends SelectionList<ListEntry> {

    private final Map<ResourceLocation, OptionDisplay> displays = new LinkedHashMap<>();
    private final List<ResourceLocation> availableOptions = new ArrayList<>();

    private ListEntry selected;
    private ResourceLocation selectedDisplay;

    public OptionDisplayList(int x, int y, Role role) {
        super(x, y, 212, 180, 20, item -> {}, true);
        OptionDisplayApi.API.values().forEach((key, factory) -> {
            OptionDisplay display = factory.create(role, this);
            if (display != null) {
                displays.put(key, display);
                availableOptions.add(key);
            }
        });
    }

    @Override
    public void setSelected(@Nullable ListEntry entry) {
        super.setSelected(entry);
        this.selected = entry;
    }

    public void move(int amount) {
        int index = availableOptions.indexOf(selectedDisplay);
        if (index != -1) {
            index = (index + amount) % availableOptions.size();
            setDisplay(availableOptions.get(index));
        }
    }

    public void setDisplay(ResourceLocation id) {
        OptionDisplay display = displays.get(id);
        if (display != null) {
            updateEntries(display.getDisplayEntries());
            this.selectedDisplay = id;
        }
    }

    @Nullable
    @Override
    public GuiEventListener getFocused() {
        return selected != null ? selected : super.getFocused();
    }

    public ResourceLocation getSelectedDisplay() {
        return selectedDisplay;
    }

    public void save(Role role) {
        displays.forEach((key, display) -> {
            if (key.equals(selectedDisplay)) {
                display.save(role);
            }
        });
    }
}
