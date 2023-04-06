package earth.terrarium.prometheus.client.screens.location;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import earth.terrarium.prometheus.client.utils.ClientUtils;
import earth.terrarium.prometheus.common.menus.location.LocationType;
import earth.terrarium.prometheus.common.network.NetworkHandler;
import earth.terrarium.prometheus.common.network.messages.server.AddLocationPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class AddLocationScreen extends Screen {

    private static final Component TITLE = Component.translatable("prometheus.locations.add");
    private static final Component SAVE = Component.translatable("prometheus.locations.save");

    private final LocationType type;

    protected AddLocationScreen(LocationType type) {
        super(CommonComponents.EMPTY);
        this.type = type;
    }

    @Override
    protected void init() {
        Window window = Minecraft.getInstance().getWindow();
        int width = window.getGuiScaledWidth();
        int height = window.getGuiScaledHeight();
        EditBox text = addRenderableWidget(new EditBox(Minecraft.getInstance().font,
                (int) ((width / 2f) - 120),
                (int) ((height / 2f) - 30), 240, 20, Component.empty()));
        addRenderableWidget(
            Button.builder(CommonComponents.GUI_CANCEL, button -> ClientUtils.sendCommand(type.editPrefix()))
                .bounds((int) ((width / 2f) - 120), (int) ((height / 2f) + 10), 100, 20)
                .build()
        );
        addRenderableWidget(
                Button.builder(SAVE, button -> NetworkHandler.CHANNEL.sendToServer(new AddLocationPacket(type, text.getValue())))
                .bounds((int) ((width / 2f) + 20), (int) ((height / 2f) + 10), 100, 20)
                .build()
        );
    }

    @Override
    public void render(@NotNull PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        super.renderBackground(stack);
        super.render(stack, mouseX, mouseY, partialTicks);
        this.font.drawShadow(stack, TITLE, (width / 2f) - 120, (height / 2f) - 45, 0xFFFFFF);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
