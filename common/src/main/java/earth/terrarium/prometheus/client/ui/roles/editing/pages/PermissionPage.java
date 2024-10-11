package earth.terrarium.prometheus.client.ui.roles.editing.pages;

import com.teamresourceful.resourcefullib.common.utils.TriState;
import earth.terrarium.olympus.client.components.Widgets;
import earth.terrarium.olympus.client.components.compound.radio.RadioState;
import earth.terrarium.olympus.client.components.renderers.WidgetRenderers;
import earth.terrarium.olympus.client.components.string.MultilineTextWidget;
import earth.terrarium.olympus.client.components.textbox.autocomplete.AutocompleteTextBox;
import earth.terrarium.olympus.client.ui.UIIcons;
import earth.terrarium.olympus.client.utils.StateUtils;
import earth.terrarium.prometheus.api.permissions.PermissionApi;
import earth.terrarium.prometheus.api.roles.client.Page;
import earth.terrarium.prometheus.common.constants.ConstantComponents;
import earth.terrarium.prometheus.common.handlers.role.Role;
import earth.terrarium.prometheus.common.menus.content.RoleEditContent;
import earth.terrarium.prometheus.common.network.NetworkHandler;
import earth.terrarium.prometheus.common.network.messages.server.roles.ServerboundSaveRolePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.*;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.function.Consumers;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PermissionPage implements Page {

    private final RoleEditContent content;
    private final Runnable refresh;

    private AutocompleteTextBox<String> permissionBox;

    public PermissionPage(RoleEditContent content, Runnable refresh) {
        this.content = content;
        this.refresh = refresh;
    }

    @Override
    public boolean canSave() {
        return false;
    }

    @Override
    public Layout getContents(int width, int height) {
        GridLayout layout = new GridLayout().rowSpacing(5);
        LinearLayout header = LinearLayout.horizontal();

        header.addChild(
            new MultilineTextWidget(width / 2, ConstantComponents.PERMISSIONS_TITLE, Minecraft.getInstance().font).alignLeft(),
            layout.newCellSettings().alignVerticallyMiddle()
        );

        permissionBox = header.addChild(
            new AutocompleteTextBox<>(
                permissionBox,
                "",
                width / 2 - 35, 20,
                PermissionApi.API.getAutoComplete(),
                this::filterOutSuggestions,
                Objects::toString
            )
        );

        header.addChild(new SpacerElement(5, 0));
        header.addChild(Widgets.button(button -> {
            button.withSize(30, 20)
                .withRenderer(WidgetRenderers.text(ConstantComponents.ADD))
                .withCallback(() -> {
                    Role role = this.content.selected();
                    role.permissions().put(permissionBox.getRawValue(), TriState.UNDEFINED);
                    this.refresh.run();
                    NetworkHandler.CHANNEL.sendToServer(new ServerboundSaveRolePacket(this.content.selectedId(), this.content.selected()));
                    this.permissionBox.clear();
                });
        }));

        header.arrangeElements();
        layout.addChild(header, 0, 0);

        layout.addChild(
            Widgets.list(list -> {
                list.withSize(width, height - header.getHeight() - 5);
                list.withContentFillWidth();
                list.withContents(gridViewLayout -> content.selected().permissions().forEach((s, triStateState) -> {
                    gridViewLayout.withChild(Widgets.frame(frame -> frame.withStretchToContentHeight()
                        .withWidthCallback((frameWidget, frameLayout) -> frameLayout.setMinWidth(frameWidget.getViewWidth()))
                        .withStretchToContentHeight()
                        .withContents(frameLayout -> {
                            frameLayout.addChild(new StringWidget(Component.literal(s), Minecraft.getInstance().font), LayoutSettings::alignHorizontallyLeft);
                            frameLayout.addChild(Widgets.tristate(StateUtils.tristate(triStateState), builder -> {
                                builder.withCallback(state -> {
                                    content.selected().permissions().put(s, state);
                                    refresh.run();
                                    NetworkHandler.CHANNEL.sendToServer(new ServerboundSaveRolePacket(content.selectedId(), content.selected()));
                                });
                            }, Consumers.nop()), layoutSettings -> {
                                layoutSettings.alignHorizontallyRight();
                                layoutSettings.paddingRight(25);
                            });
                            frameLayout.addChild(Widgets.button(button -> {
                                button.withSize(20);
                                button.withRenderer(WidgetRenderers.icon(UIIcons.TRASH).withCentered(12, 12).withPadding(0,0, 2, 0));
                                button.withCallback(() -> {
                                    content.selected().permissions().remove(s);
                                    refresh.run();
                                    NetworkHandler.CHANNEL.sendToServer(new ServerboundSaveRolePacket(content.selectedId(), content.selected()));
                                });
                            }), LayoutSettings::alignHorizontallyRight);
                        })));
                }));
            }), 1, 0
        );

        return layout;
    }

    public boolean filterOutSuggestions(String input, String option) {
        if (this.content.selected().permissions().containsKey(option)) return false;
        boolean isCommand = option.startsWith("commands");
        if (isCommand) {
            return option.split("\\.").length <= input.split("\\.").length + 1;
        }
        return option.startsWith(input) || input.isBlank();
    }

    public void save() {
        this.refresh.run();
        NetworkHandler.CHANNEL.sendToServer(new ServerboundSaveRolePacket(this.content.selectedId(), this.content.selected()));
    }
}
