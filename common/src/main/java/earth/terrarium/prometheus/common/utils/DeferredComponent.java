package earth.terrarium.prometheus.common.utils;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class DeferredComponent implements Component {

    private final Supplier<Component> component;

    public DeferredComponent(Supplier<Component> component) {
        this.component = component;
    }

    public static DeferredComponent of(Supplier<Component> component) {
        return new DeferredComponent(component);
    }

    @Override
    public @NotNull Style getStyle() {
        return component.get().getStyle();
    }

    @Override
    public @NotNull ComponentContents getContents() {
        return component.get().getContents();
    }

    @Override
    public @NotNull String getString() {
        return component.get().getString();
    }

    @Override
    public @NotNull String getString(int maxLength) {
        return component.get().getString(maxLength);
    }

    @Override
    public @NotNull List<Component> getSiblings() {
        return component.get().getSiblings();
    }

    @Override
    public @NotNull MutableComponent plainCopy() {
        return component.get().plainCopy();
    }

    @Override
    public @NotNull MutableComponent copy() {
        return component.get().copy();
    }

    @Override
    public @NotNull FormattedCharSequence getVisualOrderText() {
        return component.get().getVisualOrderText();
    }

    @Override
    public <T> @NotNull Optional<T> visit(StyledContentConsumer<T> acceptor, Style style) {
        return component.get().visit(acceptor, style);
    }

    @Override
    public <T> @NotNull Optional<T> visit(ContentConsumer<T> acceptor) {
        return component.get().visit(acceptor);
    }

    @Override
    public @NotNull List<Component> toFlatList() {
        return component.get().toFlatList();
    }

    @Override
    public @NotNull List<Component> toFlatList(Style style) {
        return component.get().toFlatList(style);
    }

    @Override
    public boolean contains(Component other) {
        return component.get().contains(other);
    }
}
