package earth.terrarium.prometheus.client.screens.roles.options.entries;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefullib.client.components.selection.ListEntry;
import com.teamresourceful.resourcefullib.client.components.selection.SelectionList;
import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import com.teamresourceful.resourcefullib.client.screens.CursorScreen;
import com.teamresourceful.resourcefullib.client.utils.CursorUtils;
import com.teamresourceful.resourcefullib.client.utils.RenderUtils;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.api.permissions.PermissionApi;
import earth.terrarium.prometheus.client.utils.ClientUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class PermissionHeaderListEntry extends TextBoxListEntry {

    private static final ResourceLocation TEXTURE = new ResourceLocation(Prometheus.MOD_ID, "textures/gui/buttons.png");
    private final Consumer<String> onPress;
    private final SelectionList<ListEntry> list;

    private String lastAutoComplete = "";
    private List<String> lastAutoCompleteList = new ArrayList<>();

    public PermissionHeaderListEntry(Component component, SelectionList<ListEntry> list, Consumer<String> onPress) {
        super("", 100, component, s -> true);
        this.list = list;
        this.onPress = onPress;
    }

    @Override
    protected void render(@NotNull ScissorBoxStack scissorStack, @NotNull PoseStack stack, int id, int left, int top, int width, int height, int mouseX, int mouseY, boolean hovered, float partialTick, boolean selected) {
        Gui.drawString(stack, Minecraft.getInstance().font, component, left + 7, top + 7, 0xFFFFFF);
        RenderUtils.bindTexture(TEXTURE);
        boolean btnHovered = mouseX >= left + width - 14 - 7 && mouseX < left + width - 7 && mouseY >= top + 3 && mouseY < top + 17;
        Gui.blit(stack, left + width - 14 - 7, top + 3, 0, canAdd() ? btnHovered ? 63 : 49 : 77, 14, 14);
        if (btnHovered) {
            CursorUtils.setCursor(true, canAdd() ? CursorScreen.Cursor.POINTER : CursorScreen.Cursor.DISABLED);
            ClientUtils.setTooltip(Component.literal("Add a Permission"));
        }

        MutableComponent tempText = Component.literal(text);

        if (selected) {
            String autoComplete = getAutoComplete();
            if (!autoComplete.isEmpty()) {
                tempText.append(Component.literal(autoComplete).withStyle(ChatFormatting.GRAY));
            } else {
                boolean show = System.currentTimeMillis() / 500 % 2 == 0;
                tempText.append(Component.literal("_").withStyle(show ? ChatFormatting.RESET : ChatFormatting.BLACK));
            }
        }

        TextBoxListEntry.renderTextBox(scissorStack, stack, left + (width / 2) + 3, top + 3, width / 2 - 29, 14, tempText, selected ? 0xFFFFFFFF : 0xFF505050);

        if (mouseX >= left + (width / 2) - 3 && mouseX < left + width - 14 - 9 && mouseY >= top + 3 && mouseY < top + 17) {
            CursorUtils.setCursor(true, CursorScreen.Cursor.TEXT);
        }

        Gui.fill(stack, left + 5, top + 19, left + width - 5, top + 20, 0xFF505050);
    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        if (button == 0) {
            if (x >= 191 && x < 205 && y >= 3 && y < 17 && canAdd()) {
                this.onPress.accept(text);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        if (i == GLFW.GLFW_KEY_TAB) {
            if (lastAutoCompleteList.size() > 1) {
                int newIndex = lastAutoCompleteList.indexOf(lastAutoComplete) + 1;
                lastAutoComplete = lastAutoCompleteList.get(newIndex % lastAutoCompleteList.size());
            }
            return true;
        }
        if (i == GLFW.GLFW_KEY_ENTER) {
            String autoComplete = getAutoComplete();
            if (!autoComplete.isEmpty()) {
                text += autoComplete;
            }
            return true;
        }
        return super.keyPressed(i, j, k);
    }

    private String getAutoComplete() {
        List<String> perms = PermissionApi.API.getAutoComplete(text);
        if (Objects.equals(perms, lastAutoCompleteList)) {
            return lastAutoComplete;
        }
        lastAutoCompleteList = perms;
        lastAutoComplete = perms.isEmpty() ? "" : perms.get(0);
        return lastAutoComplete;
    }

    private boolean canAdd() {
        if (text.isBlank()) return false;
        for (ListEntry child : this.list.children()) {
            if (child instanceof PermissionListEntry entry) {
                if (entry.permission().equals(text)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void setFocused(boolean bl) {

    }

    @Override
    public boolean isFocused() {
        return false;
    }
}
