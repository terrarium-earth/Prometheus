package earth.terrarium.prometheus.client.ui.roles.editing.pages.permissions;

import com.teamresourceful.resourcefullib.common.utils.TriState;
import earth.terrarium.olympus.client.components.buttons.TextButton;
import earth.terrarium.olympus.client.components.string.MultilineTextWidget;
import earth.terrarium.olympus.client.components.textbox.autocomplete.AutocompleteTextBox;
import earth.terrarium.prometheus.api.permissions.PermissionApi;
import earth.terrarium.prometheus.api.roles.client.Page;
import earth.terrarium.prometheus.common.constants.ConstantComponents;
import earth.terrarium.prometheus.common.handlers.role.Role;
import earth.terrarium.prometheus.common.menus.content.RoleEditContent;
import earth.terrarium.prometheus.common.network.NetworkHandler;
import earth.terrarium.prometheus.common.network.messages.server.roles.ServerboundSaveRolePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.layouts.SpacerElement;

import java.util.Objects;

public class PermissionPage implements Page {

    private final RoleEditContent content;
    private final Runnable refresh;

    private AutocompleteTextBox<String> permissionBox;
    private PermissionList list;

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
        header.addChild(TextButton.create(
            30, 20,
            ConstantComponents.PERMISSIONS_ADD, b -> {
                Role role = this.content.selected();
                role.permissions().put(this.permissionBox.getRawValue(), TriState.UNDEFINED);
                this.refresh.run();
                NetworkHandler.CHANNEL.sendToServer(new ServerboundSaveRolePacket(this.content.selectedId(), this.content.selected()));
                this.permissionBox.clear();
            }
        ));

        header.arrangeElements();
        layout.addChild(header, 0, 0);

        this.list = layout.addChild(
            new PermissionList(this.list, width, height - header.getHeight() - 10, this.content.selected(), () -> {
                this.refresh.run();
                NetworkHandler.CHANNEL.sendToServer(new ServerboundSaveRolePacket(this.content.selectedId(), this.content.selected()));
            }),
            1, 0
        );
        this.list.update();

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
