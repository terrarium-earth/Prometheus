package earth.terrarium.prometheus.client.ui.roles.editing.pages;

import earth.terrarium.olympus.client.components.Widgets;
import earth.terrarium.olympus.client.utils.State;
import earth.terrarium.prometheus.api.roles.client.Page;
import earth.terrarium.prometheus.client.utils.UiUtils;
import earth.terrarium.prometheus.common.constants.ConstantComponents;
import earth.terrarium.prometheus.common.handlers.role.Role;
import earth.terrarium.prometheus.common.menus.content.RoleEditContent;
import earth.terrarium.prometheus.common.roles.HomeOptions;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.Layout;

public class HomesPage implements Page {
    private final State<Integer> maxHomesBox;

    public HomesPage(RoleEditContent content, Runnable ignored) {
        Role role = content.selected();
        HomeOptions options = role.getNonNullOption(HomeOptions.SERIALIZER);
        maxHomesBox = State.of(options.max());
    }

    @Override
    public Layout getContents(int width, int height) {
        GridLayout layout = new GridLayout().rowSpacing(5);

        UiUtils.addLine(
            layout, 0, width,
            ConstantComponents.HOMES_MAX,
            (w) -> Widgets.intInput(maxHomesBox, textBox -> textBox.withSize(w, 20))
        );

        return layout;
    }

    @Override
    public void save(Role role) {
        HomeOptions display = role.getNonNullOption(HomeOptions.SERIALIZER);
        HomeOptions options = new HomeOptions(maxHomesBox.get());
        if (options.max() > 0 && options.max() != display.max()) {
            role.setData(options);
        }
    }
}
