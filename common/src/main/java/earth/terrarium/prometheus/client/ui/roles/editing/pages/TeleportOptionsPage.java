package earth.terrarium.prometheus.client.ui.roles.editing.pages;

import earth.terrarium.olympus.client.components.Widgets;
import earth.terrarium.olympus.client.utils.State;
import earth.terrarium.prometheus.api.roles.client.Page;
import earth.terrarium.prometheus.client.utils.UiUtils;
import earth.terrarium.prometheus.common.constants.ConstantComponents;
import earth.terrarium.prometheus.common.handlers.role.Role;
import earth.terrarium.prometheus.common.menus.content.RoleEditContent;
import earth.terrarium.prometheus.common.roles.TeleportOptions;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.Layout;

public class TeleportOptionsPage implements Page {

    private final State<Integer> expireBox;
    private final State<Integer> cooldownBox;
    private final State<Integer> rtpDistanceBox;

    public TeleportOptionsPage(RoleEditContent content, Runnable ignored) {
        Role role = content.selected();
        TeleportOptions options = role.getNonNullOption(TeleportOptions.SERIALIZER);
        expireBox = State.of(options.expire());
        cooldownBox = State.of(options.rtpCooldown());
        rtpDistanceBox = State.of(options.rtpDistance());
    }

    @Override
    public Layout getContents(int width, int height) {
        GridLayout layout = new GridLayout().rowSpacing(5);

        UiUtils.addLine(
            layout, 0, width,
            ConstantComponents.REQUEST_TIMEOUT,
            (w) -> Widgets.intInput(expireBox, textBox -> {
                textBox.withSize(w, 20);
                textBox.withTooltip(ConstantComponents.REQUEST_TIMEOUT_TOOLTIP);
            })
        );

        UiUtils.addLine(
            layout, 1, width,
            ConstantComponents.RTP_COOLDOWN,
            (w) -> Widgets.intInput(cooldownBox, textBox -> {
                textBox.withSize(w, 20);
                textBox.withTooltip(ConstantComponents.RTP_COOLDOWN_TOOLTIP);
            })
        );

        UiUtils.addLine(
            layout, 2, width,
            ConstantComponents.RTP_DISTANCE,
            (w) -> Widgets.intInput(rtpDistanceBox, textBox -> {
                textBox.withSize(w, 20);
                textBox.withTooltip(ConstantComponents.RTP_DISTANCE_TOOLTIP);
            })
        );

        return layout;
    }

    @Override
    public void save(Role role) {
        TeleportOptions options = role.getNonNullOption(TeleportOptions.SERIALIZER);
        TeleportOptions newOptions = new TeleportOptions(expireBox.get(), cooldownBox.get(), rtpDistanceBox.get());
        if (!newOptions.equals(options)) {
            role.setData(newOptions);
        }
    }
}
