package earth.terrarium.prometheus.client.ui.roles.editing.pages;

import earth.terrarium.olympus.client.components.buttons.TextButton;
import earth.terrarium.prometheus.api.roles.client.Page;
import earth.terrarium.prometheus.api.roles.client.PageApi;
import earth.terrarium.prometheus.client.ui.roles.editing.RoleEditingScreen;
import earth.terrarium.prometheus.client.utils.UiUtils;
import earth.terrarium.prometheus.common.constants.ConstantComponents;
import earth.terrarium.prometheus.common.menus.content.RoleEditContent;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public record OptionsPage(RoleEditContent content, Runnable refresh) implements Page {

    @Override
    public Layout getContents(int width, int height) {
        GridLayout layout = new GridLayout().rowSpacing(5);

        int i = 0;
        for (var entry : PageApi.API.values().entrySet()) {
            ResourceLocation id = entry.getKey();
            var factory = entry.getValue();
            Component title = Component.translatable(
                id.toLanguageKey("option")
            );
            UiUtils.addLine(
                layout, i, width,
                title,
                (w) -> TextButton.create(w, 20, ConstantComponents.EDIT, b ->
                    RoleEditingScreen.open(content, factory)
                )
            );
            i++;
        }

        return layout;
    }

    @Override
    public boolean canSave() {
        return false;
    }
}
