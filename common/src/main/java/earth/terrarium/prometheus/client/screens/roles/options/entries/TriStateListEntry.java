package earth.terrarium.prometheus.client.screens.roles.options.entries;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefullib.client.components.selection.ListEntry;
import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import com.teamresourceful.resourcefullib.client.screens.CursorScreen;
import com.teamresourceful.resourcefullib.client.utils.CursorUtils;
import com.teamresourceful.resourcefullib.client.utils.RenderUtils;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.api.TriState;
import earth.terrarium.prometheus.client.utils.ClientUtils;
import earth.terrarium.prometheus.common.constants.ConstantComponents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class TriStateListEntry extends ListEntry {

    private static final ResourceLocation TEXTURE = new ResourceLocation(Prometheus.MOD_ID, "textures/gui/buttons.png");

    private TriState state;
    private final Component component;
    private final Consumer<TriStateListEntry> onPress;

    public TriStateListEntry(Component component, TriState state, Consumer<TriStateListEntry> onPress) {
        this.component = component;
        this.state = state;
        this.onPress = onPress;
    }

    @Override
    protected void render(@NotNull ScissorBoxStack scissorStack, @NotNull PoseStack stack, int id, int left, int top, int width, int height, int mouseX, int mouseY, boolean hovered, float partialTick, boolean selected) {
        Minecraft.getInstance().font.draw(stack, component, left + 6, top + 6, 0xFFFFFF);
        RenderUtils.bindTexture(TEXTURE);

        int startX = left + width - 34 - 5 - 16;

        Gui.blit(stack, startX, top + 4, 0, 24, 34, 12);
        switch (state) {
            case FALSE -> Gui.blit(stack, startX, top + 4, 0, 12, 12, 12);
            case UNDEFINED -> Gui.blit(stack, startX + 11, top + 4, 12, 12, 12, 12);
            case TRUE -> Gui.blit(stack, startX + 22, top + 4, 24, 12, 12, 12);
        }
        if (hovered && mouseY >= top + 4 && mouseY <= top + 15) {
            if (mouseX >= startX && mouseX < startX + 11) {
                Gui.blit(stack, startX, top + 4, 0, 0, 12, 12);
                CursorUtils.setCursor(true, CursorScreen.Cursor.POINTER);
            }
            if (mouseX > startX + 11 && mouseX < startX + 22) {
                Gui.blit(stack, startX + 11, top + 4, 12, 0, 12, 12);
                CursorUtils.setCursor(true, CursorScreen.Cursor.POINTER);
            }
            if (mouseX > startX + 22 && mouseX <= startX + 33) {
                Gui.blit(stack, startX + 22, top + 4, 24, 0, 12, 12);
                CursorUtils.setCursor(true, CursorScreen.Cursor.POINTER);
            }
        }

        boolean btnHovered = mouseX >= left + width - 12 - 5 && mouseX <= left + width - 5 && mouseY >= top + 4 && mouseY <= top + 15;
        Gui.blit(stack, left + width - 12 - 5, top + 4, 19,  btnHovered ? 61 : 49, 12, 12);
        if (btnHovered) {
            CursorUtils.setCursor(true, CursorScreen.Cursor.POINTER);
            ClientUtils.setTooltip(ConstantComponents.REMOVE);
        }
    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        if (button == InputConstants.MOUSE_BUTTON_LEFT) {
            int width = 212;
            int height = 12;

            int startX = width - 34 - 5 - 16;

            if (x >= 0 && x <= width && y >= 4 && y <= height + 4) {
                if (x >= startX && x < startX + 11) {
                    state = TriState.FALSE;
                    return true;
                }
                if (x > startX + 12 && x < startX + 22) {
                    state = TriState.UNDEFINED;
                    return true;
                }
                if (x > startX + 23 && x <= startX + 34) {
                    state = TriState.TRUE;
                    return true;
                }
                if (x >= width - 12 - 5 && x <= width - 5) {
                    onPress.accept(this);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void setFocused(boolean bl) {}

    @Override
    public boolean isFocused() {
        return false;
    }

    public TriState state() {
        return state;
    }
}