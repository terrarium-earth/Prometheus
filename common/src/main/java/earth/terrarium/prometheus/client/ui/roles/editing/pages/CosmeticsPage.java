package earth.terrarium.prometheus.client.ui.roles.editing.pages;

import earth.terrarium.olympus.client.components.Widgets;
import earth.terrarium.olympus.client.utils.State;
import earth.terrarium.prometheus.api.roles.client.Page;
import earth.terrarium.prometheus.client.utils.UiUtils;
import earth.terrarium.prometheus.common.constants.ConstantComponents;
import earth.terrarium.prometheus.common.handlers.role.Role;
import earth.terrarium.prometheus.common.menus.content.RoleEditContent;
import earth.terrarium.prometheus.common.roles.CosmeticOptions;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.Layout;

public class CosmeticsPage implements Page {
    private final State<String> nameBox;
    private final State<String> iconBox;

    public CosmeticsPage(RoleEditContent content, Runnable ignored) {
        Role role = content.selected();
        CosmeticOptions display = role.getNonNullOption(CosmeticOptions.SERIALIZER);
        this.nameBox = State.of(display.display());
        this.iconBox = State.of(display.icon());
    }

    @Override
    public Layout getContents(int width, int height) {
        GridLayout layout = new GridLayout().rowSpacing(5);
        UiUtils.addLine(layout, 0, width,
            ConstantComponents.COSMETIC_ROLE_NAME,
            w -> Widgets.textInput(nameBox, textBox -> textBox.withSize((int) (w * 0.8f), 20))
        );

        UiUtils.addLine(layout, 1, width,
            ConstantComponents.COSMETIC_ROLE_ICON,
            key -> Widgets.textInput(iconBox, textBox -> {
                textBox.withSize(20, 20);
                textBox.withFilter(text -> text.codePoints().count() == 1 || text.isBlank());
            })
        );

        return layout;
    }

    @Override
    public void save(Role role) {
        CosmeticOptions display = role.getNonNullOption(CosmeticOptions.SERIALIZER);
        CosmeticOptions newDisplay = new CosmeticOptions(
            nameBox.get(),
            iconBox.get(),
            display.color()
        );
        if (!newDisplay.display().isBlank() && !newDisplay.icon().isBlank()) {
            role.setData(newDisplay);
        }
    }
}
