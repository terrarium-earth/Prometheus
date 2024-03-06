package earth.terrarium.prometheus.client.ui.roles.editing.pages;

import earth.terrarium.olympus.client.components.textbox.IntTextBox;
import earth.terrarium.prometheus.api.roles.client.Page;
import earth.terrarium.prometheus.client.utils.UiUtils;
import earth.terrarium.prometheus.common.constants.ConstantComponents;
import earth.terrarium.prometheus.common.handlers.role.Role;
import earth.terrarium.prometheus.common.menus.content.RoleEditContent;
import earth.terrarium.prometheus.common.roles.TeleportOptions;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.Layout;

public class TeleportOptionsPage implements Page {

    private final RoleEditContent content;

    private IntTextBox expireBox;
    private IntTextBox cooldownBox;
    private IntTextBox rtpDistanceBox;

    public TeleportOptionsPage(RoleEditContent content, Runnable ignored) {
        this.content = content;
    }

    @Override
    public Layout getContents(int width, int height) {
        GridLayout layout = new GridLayout().rowSpacing(5);

        Role role = content.selected();
        TeleportOptions options = role.getNonNullOption(TeleportOptions.SERIALIZER);

        expireBox = UiUtils.addLine(
            layout, 0, width,
            ConstantComponents.REQUEST_TIMEOUT,
            (w) -> new IntTextBox(
                expireBox,
                w, 20,
                options.expire(), i -> {}
            )
        );
        expireBox.setTooltip(Tooltip.create(ConstantComponents.REQUEST_TIMEOUT_TOOLTIP));

        cooldownBox = UiUtils.addLine(
            layout, 1, width,
            ConstantComponents.RTP_COOLDOWN,
            (w) -> new IntTextBox(
                cooldownBox,
                w, 20,
                options.rtpCooldown(), i -> {}
            )
        );
        cooldownBox.setTooltip(Tooltip.create(ConstantComponents.RTP_COOLDOWN_TOOLTIP));

        rtpDistanceBox = UiUtils.addLine(
            layout, 2, width,
            ConstantComponents.RTP_DISTANCE,
            (w) -> new IntTextBox(
                rtpDistanceBox,
                w, 20,
                options.rtpDistance(), i -> {}
            )
        );
        rtpDistanceBox.setTooltip(Tooltip.create(ConstantComponents.RTP_DISTANCE_TOOLTIP));

        return layout;
    }

    @Override
    public void save(Role role) {
        TeleportOptions options = role.getNonNullOption(TeleportOptions.SERIALIZER);
        TeleportOptions newOptions = new TeleportOptions(
            expireBox.getIntValue().orElse(options.expire()),
            cooldownBox.getIntValue().orElse(options.rtpCooldown()),
            rtpDistanceBox.getIntValue().orElse(options.rtpDistance())
        );
        if (!newOptions.equals(options)) {
            role.setData(newOptions);
        }
    }
}
