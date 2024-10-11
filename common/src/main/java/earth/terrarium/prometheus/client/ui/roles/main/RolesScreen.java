package earth.terrarium.prometheus.client.ui.roles.main;

import com.teamresourceful.resourcefullib.common.utils.TriState;
import earth.terrarium.olympus.client.components.Widgets;
import earth.terrarium.olympus.client.components.renderers.WidgetRenderers;
import earth.terrarium.olympus.client.ui.UIConstants;
import earth.terrarium.olympus.client.ui.modals.BaseModal;
import earth.terrarium.prometheus.common.constants.ConstantComponents;
import earth.terrarium.prometheus.common.handlers.role.RoleEntry;
import earth.terrarium.prometheus.common.menus.content.RolesContent;
import earth.terrarium.prometheus.common.network.NetworkHandler;
import earth.terrarium.prometheus.common.network.messages.server.roles.ServerboundAddRolePacket;
import earth.terrarium.prometheus.common.network.messages.server.roles.ServerboundOpenRolePacket;
import earth.terrarium.prometheus.common.roles.CosmeticOptions;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.layouts.SpacerElement;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.util.List;

public class RolesScreen extends BaseModal {

    private final RolesContent content;

    public RolesScreen(RolesContent content, Screen screen) {
        super(CommonComponents.EMPTY, screen);
        this.content = content;

    }

    @Override
    protected void init() {
        super.init();

        LinearLayout layout = LinearLayout.vertical().spacing(5);

        LinearLayout header = LinearLayout.horizontal();

        int width = this.modalContentWidth - 20;

        header.addChild(new SpacerElement(width / 2, 0));

        header.addChild(new SpacerElement(width / 2 - 30, 0));
        header.addChild(Widgets.button(button -> {
            button.withSize(30, 20);
            button.withRenderer(WidgetRenderers.text(ConstantComponents.ADD));
            button.withCallback(() -> NetworkHandler.CHANNEL.sendToServer(new ServerboundAddRolePacket()));
        }));

        header.arrangeElements();
        layout.addChild(header);

        layout.addChild(Widgets.list(widget -> {
            widget.withSize(width, this.modalContentHeight - header.getHeight() - INNER_PADDING);
            widget.withContentFillWidth();
            widget.withScrollableY(TriState.UNDEFINED);
            widget.withContents(list -> {
                List<RoleEntry> roles = this.content.getRoles();
                for (int i = 0; i < roles.size(); i++) {
                    RoleEntry role = roles.get(i);
                    var index = i;
                    list.withChild(Widgets.frame(frameWidget -> {
                        frameWidget.withWidthCallback((fWidget, frame) -> frame.setMinWidth(fWidget.getViewWidth()));
                        frameWidget.withStretchToContentHeight();
                        frameWidget.withContents(frameLayout -> {
                            frameLayout.addChild(new StringWidget(Component.literal(role.role().getNonNullOption(CosmeticOptions.SERIALIZER).display()), font), LayoutSettings::alignHorizontallyLeft);
                            frameLayout.addChild(Widgets.carousel(options -> {
                                options.withStretchToContentSize();
                                options.withContents(optionsLayout -> {
                                    if (!role.id().equals(Util.NIL_UUID) && index != 0) {
                                        optionsLayout.withChild(Widgets.button(button -> {
                                            button.withSize(13, 15);
                                            button.withTexture(UIConstants.LIST_UP);
                                            button.withCallback(() -> {
                                                RoleEntry temp = roles.get(index - 1);
                                                roles.set(index - 1, role);
                                                roles.set(index, temp);
                                                this.rebuildWidgets();
                                            });
                                        }));
                                    }

                                    if (!role.id().equals(Util.NIL_UUID) && index != roles.size() - 2) {
                                        optionsLayout.withChild(Widgets.button(button -> {
                                            button.withSize(13, 15);
                                            button.withTexture(UIConstants.LIST_DOWN);
                                            button.withCallback(() -> {
                                                RoleEntry temp = roles.get(index + 1);
                                                roles.set(index + 1, role);
                                                roles.set(index, temp);
                                                this.rebuildWidgets();
                                            });
                                        }));
                                    }

                                    optionsLayout.withChild(Widgets.button(button -> {
                                        button.withSize(13, 15);
                                        button.withTexture(UIConstants.LIST_EDIT);
                                        button.withCallback(() -> {
                                            NetworkHandler.CHANNEL.sendToServer(new ServerboundOpenRolePacket(role.id()));
                                            this.rebuildWidgets();
                                        });
                                    }));

                                    if (!role.id().equals(Util.NIL_UUID)) {
                                        optionsLayout.withChild(Widgets.button(button -> {
                                            button.withSize(13, 15);
                                            button.withTexture(UIConstants.LIST_DELETE);
                                            button.withCallback(() -> {
                                                this.content.remove(role.id());
                                                this.rebuildWidgets();
                                            });
                                        }));
                                    }
                                });
                            }), LayoutSettings::alignHorizontallyRight);
                        });
                    }));
                }
            });
        }));

        layout.arrangeElements();
        layout.setPosition(this.modalContentLeft + 10, this.modalContentTop);
        layout.visitWidgets(this::addRenderableWidget);
    }

    @Override
    protected GridLayout initButtons(int position) {
        GridLayout layout = super.initButtons(position + 1);
        layout.addChild(
            new ImageButton(11, 11, UIConstants.MODAL_SAVE, b -> {
                this.content.save();
                this.rebuildWidgets();
            }, ConstantComponents.SAVE),
            0, position
        ).setTooltip(Tooltip.create(ConstantComponents.SAVE));
        return layout;
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderTransparentBackground(graphics);
        graphics.blitSprite(UIConstants.MODAL, this.left, this.top, this.modalWidth, this.modalHeight);
        graphics.blitSprite(UIConstants.MODAL_HEADER, this.left, this.top, this.modalWidth, TITLE_BAR_HEIGHT);
    }

    public void renderForeground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (!this.content.areRolesDifferent()) return;
        graphics.drawString(
            this.font,
            ConstantComponents.UNSAVED_CHANGES, this.left + INNER_PADDING * 2, (int) (this.top + (TITLE_BAR_HEIGHT - 9) / 2f) + 1,
            0xffffffff, false
        );
    }

    public static void open(RolesContent content) {
        Screen screen = Minecraft.getInstance().screen;
        if (screen instanceof RolesScreen) {
            screen = null;
        }
        Minecraft.getInstance().setScreen(new RolesScreen(content, screen));
    }
}
