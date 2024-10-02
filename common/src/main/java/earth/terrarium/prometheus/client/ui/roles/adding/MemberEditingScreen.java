package earth.terrarium.prometheus.client.ui.roles.adding;

import earth.terrarium.olympus.client.components.Widgets;
import earth.terrarium.olympus.client.components.dropdown.DropdownState;
import earth.terrarium.olympus.client.components.renderers.WidgetRenderers;
import earth.terrarium.olympus.client.ui.UIConstants;
import earth.terrarium.olympus.client.ui.UIIcons;
import earth.terrarium.olympus.client.ui.modals.BaseModal;
import earth.terrarium.prometheus.common.constants.ConstantComponents;
import earth.terrarium.prometheus.common.menus.content.MemberRolesContent;
import it.unimi.dsi.fastutil.objects.Object2BooleanLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.layouts.SpacerElement;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.function.Consumers;

import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MemberEditingScreen extends BaseModal {

    private final MemberRolesContent content;
    private final Object2BooleanMap<UUID> roles = new Object2BooleanLinkedOpenHashMap<>();

    private final DropdownState<MemberRolesContent.MemberRole> roleDropdown = DropdownState.empty();

    public MemberEditingScreen(MemberRolesContent content, Screen screen) {
        super(getTitle(content), screen);
        this.content = content;
        for (var role : content.roles()) {
            this.roles.put(role.id(), role.selected());
        }
    }

    @Override
    protected void init() {
        super.init();

        LinearLayout layout = LinearLayout.vertical();

        LinearLayout header = LinearLayout.horizontal();

        int width = this.modalContentWidth - 20;

        header.addChild(
            new SpacerElement(width / 2, 0)
        );

        Map<MemberRolesContent.MemberRole, Component> options = content.roles().stream()
            .filter(role -> !roles.getOrDefault(role.id(), false))
            .collect(Collectors.toMap(
                Function.identity(),
                role -> Component.literal(role.name())
            ));

        var dropdownBtn = header.addChild(Widgets.dropdown(this.roleDropdown,
            content.roles(),
            memberRole -> Component.literal(memberRole.name()),
            button -> button.withSize(width / 2 - 35, 20),
            Consumers.nop()
        ));

        dropdownBtn.active = !options.isEmpty();

        header.addChild(new SpacerElement(5, 0));

        header.addChild(Widgets.button(button -> {
            button.withSize(30, 20)
                .withRenderer(WidgetRenderers.text(ConstantComponents.ADD))
                .withCallback(() -> {
                    MemberRolesContent.MemberRole role = this.roleDropdown.get();
                    if (role == null) return;
                    this.roles.put(role.id(), true);
                    this.rebuildWidgets();
                });
        }));

        layout.addChild(header);

        layout.addChild(Widgets.list(widget -> {
            widget.withSize(this.modalContentWidth - 20, this.modalContentHeight);
            widget.withContents(list -> {
                content.roles().forEach(member -> {
                    list.withChild(Widgets.labelled(font, Component.literal(member.name()), Widgets.button(button -> {
                        button.withSize(16, 16)
                            .withRenderer(WidgetRenderers.icon(UIIcons.TRASH).withCentered(12, 12))
                            .withCallback(() -> {
                                this.roles.put(member.id(), false);
                                this.rebuildWidgets();
                            });
                    })));
                });
            });
        }));

        layout.arrangeElements();
        layout.setPosition(this.modalContentLeft + 10, this.modalContentTop);
        layout.visitWidgets(this::addRenderableWidget);
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderTransparentBackground(graphics);
        graphics.blitSprite(UIConstants.MODAL, this.left, this.top, this.modalWidth, this.modalHeight);
        graphics.blitSprite(UIConstants.MODAL_HEADER, this.left, this.top, this.modalWidth, TITLE_BAR_HEIGHT);
    }

    private static Component getTitle(MemberRolesContent content) {
        ClientPacketListener con = Minecraft.getInstance().getConnection();
        String person = content.person().toString();
        if (con != null) {
            PlayerInfo info = con.getPlayerInfo(content.person());
            if (info != null) {
                person = info.getProfile().getName();
            }
        }

        return Component.translatable("prometheus.member.edit", person);
    }

    public static void open(MemberRolesContent content) {
        Minecraft.getInstance().setScreen(new MemberEditingScreen(content, null));
    }
}
