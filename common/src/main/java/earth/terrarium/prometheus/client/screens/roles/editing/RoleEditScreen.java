package earth.terrarium.prometheus.client.screens.roles.editing;

import com.teamresourceful.resourcefullib.client.screens.BaseCursorScreen;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.common.constants.ConstantComponents;
import earth.terrarium.prometheus.common.handlers.role.RoleEntry;
import earth.terrarium.prometheus.common.menus.content.RoleEditContent;
import earth.terrarium.prometheus.common.network.NetworkHandler;
import earth.terrarium.prometheus.common.network.messages.server.roles.OpenRolePacket;
import earth.terrarium.prometheus.common.network.messages.server.roles.OpenRolesPacket;
import earth.terrarium.prometheus.common.network.messages.server.roles.SaveRolePacket;
import earth.terrarium.prometheus.common.roles.CosmeticOptions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class RoleEditScreen extends BaseCursorScreen {

    private static final ResourceLocation CONTAINER_BACKGROUND = new ResourceLocation(Prometheus.MOD_ID, "textures/gui/edit_role.png");
    private static final ResourceLocation BUTTONS = new ResourceLocation(Prometheus.MOD_ID, "textures/gui/buttons.png");
    private static final int HEIGHT = 223;
    private static final int WIDTH = 276;

    private final RoleEditContent content;

    private QuickEditList list;
    private OptionDisplayList displayList;

    public RoleEditScreen(RoleEditContent content) {
        super(CommonComponents.EMPTY);
        this.content = content;
    }

    @Override
    protected void init() {
        int leftPos = (this.width - WIDTH) / 2;
        int topPos = (this.height - HEIGHT) / 2;
        super.init();

        this.list = addRenderableWidget(new QuickEditList(leftPos + 8, topPos + 29, 42, 180, 20, item -> {
            if (item != null && !item.id().equals(this.content.selectedId())) {
                NetworkHandler.CHANNEL.sendToServer(new OpenRolePacket(item.id()));
            }
        }));
        this.list.update(this.content.roles());

        this.displayList = addRenderableWidget(new OptionDisplayList(leftPos + 56, topPos + 29, this.content.selected()));
        this.displayList.setDisplay(CosmeticOptions.SERIALIZER.id());

        addRenderableOnly(new SelectedOptionWidget(leftPos + 77, topPos + 15, 170, 10, this.displayList));

        addRenderableWidget(new ImageButton(leftPos + 61, topPos + 15, 12, 12, 276, 0, 12, CONTAINER_BACKGROUND, 512, 512, button ->
            this.displayList.move(-1)
        )).setTooltip(Tooltip.create(ConstantComponents.PREV));

        addRenderableWidget(new ImageButton(leftPos + 251, topPos + 15, 12, 12, 288, 0, 12, CONTAINER_BACKGROUND, 512, 512, button ->
            this.displayList.move(1)
        )).setTooltip(Tooltip.create(ConstantComponents.NEXT));

        addRenderableWidget(new ImageButton(leftPos + 7, topPos + 7, 17, 17, 0, 97, 17, BUTTONS, button -> {
            if (Minecraft.getInstance().gameMode != null) {
                NetworkHandler.CHANNEL.sendToServer(new OpenRolesPacket());
            }
        })).setTooltip(Tooltip.create(ConstantComponents.BACK));

        addRenderableWidget(new ImageButton(leftPos + 34, topPos + 7, 17, 17, 18, 97, 17, BUTTONS, button -> {
            this.displayList.save(this.content.selected());
            int index = this.content.getIndexOf(this.content.selectedId());
            if (index != -1) {
                this.content.roles().set(index, new RoleEntry(this.content.selectedId(), this.content.selected()));
            }
            NetworkHandler.CHANNEL.sendToServer(new SaveRolePacket(this.content.selectedId(), this.content.selected()));
            this.list.update(this.content.roles());
        })).setTooltip(Tooltip.create(ConstantComponents.SAVE));
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int i, int j, float f) {
        int leftPos = (this.width - WIDTH) / 2;
        int topPos = (this.height - HEIGHT) / 2;
        this.renderBackground(graphics);
        graphics.blit(CONTAINER_BACKGROUND, leftPos, topPos, 0, 0, WIDTH, HEIGHT, 512, 512);
        super.render(graphics, i, j, f);
        if (this.content.selected() == null) return;
        CosmeticOptions options = this.content.selected().getOption(CosmeticOptions.SERIALIZER);
        if (options != null) {
            Component text = Component.translatable("prometheus.roles.edit", options.display());
            graphics.drawString(
                font,
                text, leftPos + 162 - Mth.floor(font.width(text) / 2f), topPos + 6, 4210752,
                false
            );
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}