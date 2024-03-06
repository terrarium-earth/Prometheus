package earth.terrarium.prometheus.client.ui.roles.editing.pages;

import earth.terrarium.olympus.client.components.textbox.TextBox;
import earth.terrarium.prometheus.api.roles.client.Page;
import earth.terrarium.prometheus.client.utils.UiUtils;
import earth.terrarium.prometheus.common.constants.ConstantComponents;
import earth.terrarium.prometheus.common.handlers.role.Role;
import earth.terrarium.prometheus.common.menus.content.RoleEditContent;
import earth.terrarium.prometheus.common.roles.CosmeticOptions;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.Layout;

public class CosmeticsPage implements Page {

    private final RoleEditContent content;

    private TextBox nameBox;
    private TextBox iconBox;

    public CosmeticsPage(RoleEditContent content, Runnable ignored) {
        this.content = content;
    }

    @Override
    public Layout getContents(int width, int height) {
        GridLayout layout = new GridLayout().rowSpacing(5);

        Role role = content.selected();
        CosmeticOptions display = role.getNonNullOption(CosmeticOptions.SERIALIZER);

        nameBox = UiUtils.addLine(
            layout, 0, width,
            ConstantComponents.COSMETIC_ROLE_NAME,
            (w) -> new TextBox(
                nameBox,
                display.display(),
                (int) (w * 0.8f), 20,
                24
            )
        );

        iconBox = UiUtils.addLine(
            layout, 1, width,
            ConstantComponents.COSMETIC_ROLE_ICON,
            (w) -> new TextBox(
                iconBox,
                display.icon(),
                20, 20,
                Short.MAX_VALUE,
                text -> text.codePoints().count() == 1 || text.isBlank(),
                text -> {}
            )
        );

        return layout;
    }

    @Override
    public void save(Role role) {
        CosmeticOptions display = role.getNonNullOption(CosmeticOptions.SERIALIZER);
        CosmeticOptions newDisplay = new CosmeticOptions(
            nameBox.getValue(),
            iconBox.getValue(),
            display.color()
        );
        if (!newDisplay.display().isBlank() && !newDisplay.icon().isBlank()) {
            role.setData(newDisplay);
        }
    }
}
