package earth.terrarium.prometheus.client.screens.location;

import com.mojang.blaze3d.platform.Window;
import earth.terrarium.prometheus.common.constants.ConstantComponents;
import earth.terrarium.prometheus.common.menus.content.location.LocationType;
import earth.terrarium.prometheus.common.network.NetworkHandler;
import earth.terrarium.prometheus.common.network.messages.server.AddLocationPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class AddLocationScreen extends Screen {

    private static final Component TITLE = Component.translatable("prometheus.locations.add");

    private final Screen parent;
    private final LocationType type;

    protected AddLocationScreen(Screen parent, LocationType type) {
        super(CommonComponents.EMPTY);
        this.parent = parent;
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
            Button.builder(CommonComponents.GUI_CANCEL, button -> Minecraft.getInstance().setScreen(parent))
                .bounds((int) ((width / 2f) - 120), (int) ((height / 2f) + 10), 100, 20)
                .build()
        );
        addRenderableWidget(
            Button.builder(ConstantComponents.SAVE, button -> NetworkHandler.CHANNEL.sendToServer(new AddLocationPacket(type, text.getValue())))
                .bounds((int) ((width / 2f) + 20), (int) ((height / 2f) + 10), 100, 20)
                .build()
        );
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTicks);
        graphics.drawString(
            this.font,
            TITLE, (int) (width / 2f) - 120, (int) (height / 2f) - 45, 0xFFFFFF,
            false
        );
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
