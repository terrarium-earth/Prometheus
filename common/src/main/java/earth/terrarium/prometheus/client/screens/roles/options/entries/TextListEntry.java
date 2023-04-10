package earth.terrarium.prometheus.client.screens.roles.options.entries;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefullib.client.components.selection.ListEntry;
import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class TextListEntry extends ListEntry {

    private final Component component;

    public TextListEntry(Component component) {
        this.component = component;
    }

    @Override
    protected void render(@NotNull ScissorBoxStack scissorStack, @NotNull PoseStack stack, int id, int left, int top, int width, int height, int mouseX, int mouseY, boolean hovered, float partialTick, boolean selected) {
        Gui.drawCenteredString(stack, Minecraft.getInstance().font, component, left + (width / 2), top + 9, 0xFFFFFF);
        Gui.fill(stack, left + 5, top + 19, left + width - 5, top + 20, 0xFF505050);
    }

    @Override
    public void setFocused(boolean bl) {

    }

    @Override
    public boolean isFocused() {
        return false;
    }
}
