package earth.terrarium.prometheus.client.screens.base;

import com.google.common.collect.Lists;
import com.teamresourceful.resourcefullib.client.screens.BaseCursorScreen;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectRBTreeMap;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;

public class PriorityScreen extends BaseCursorScreen {

    private final Int2ObjectMap<List<GuiEventListener>> sortedChildren = new Int2ObjectRBTreeMap<>(Comparator.naturalOrder());
    private final List<GuiEventListener> cachedChildren = Lists.newArrayList();

    protected PriorityScreen(Component component) {
        super(component);
        sortedChildren.put(0, Lists.newArrayList());
    }

    protected <T extends GuiEventListener & NarratableEntry> T addWidget(int priority, T listener) {
        this.sortedChildren.computeIfAbsent(priority, (key) -> Lists.newArrayList()).add(listener);
        cacheChildren();
        return listener;
    }

    protected <T extends GuiEventListener & Renderable> T addRenderableWidget(int priority, T listener) {
        this.sortedChildren.computeIfAbsent(priority, (key) -> Lists.newArrayList()).add(listener);
        cacheChildren();
        addRenderableOnly(listener);
        return listener;
    }

    @Override
    protected void removeWidget(GuiEventListener listener) {
        super.removeWidget(listener);

        for (List<GuiEventListener> value : this.sortedChildren.values()) {
            value.remove(listener);
        }
        cacheChildren();
    }

    @Override
    protected void clearWidgets() {
        super.clearWidgets();
        this.sortedChildren.clear();
        cacheChildren();
    }

    @Override
    public @NotNull List<? extends GuiEventListener> children() {
        return this.cachedChildren;
    }

    private void cacheChildren() {
        this.cachedChildren.clear();

        for (var entry : this.sortedChildren.int2ObjectEntrySet()) {
            int priority = entry.getIntKey();
            List<GuiEventListener> listeners = entry.getValue();
            if (priority == 0) {
                this.cachedChildren.addAll(super.children());
                this.cachedChildren.addAll(listeners);
            } else {
                this.cachedChildren.addAll(listeners);
            }
        }

    }
}
