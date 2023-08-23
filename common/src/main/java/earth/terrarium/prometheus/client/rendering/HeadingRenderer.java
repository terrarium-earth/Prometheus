package earth.terrarium.prometheus.client.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.client.utils.ClientListenerHook;
import earth.terrarium.prometheus.common.handlers.heading.Heading;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

public class HeadingRenderer {

    private static final ResourceLocation TEXTURE = new ResourceLocation(Prometheus.MOD_ID, "textures/gui/heading_icons.png");

    public static void onRenderIcon(UUID player, GuiGraphics graphics, int x, int y) {
        ClientPacketListener listener = Minecraft.getInstance().getConnection();
        if (listener instanceof ClientListenerHook hook) {
            Heading heading = hook.prometheus$getHeadings().getOrDefault(player, Heading.NONE);
            if (heading.hasIcon()) {
                graphics.blit(TEXTURE, x, y, heading.getU(), heading.getV(), 8, 8, 256, 256);
            }
        }
    }

    public static Component decorateHeading(UUID player, Component text) {
        ClientPacketListener listener = Minecraft.getInstance().getConnection();
        if (listener instanceof ClientListenerHook hook) {
            Heading heading = hook.prometheus$getHeadings().getOrDefault(player, Heading.NONE);
            if (!heading.canBroadcast()) return text;
            return text.copy().withStyle(style -> style.withColor(heading.getColor()));
        }
        return text;
    }

    public static void onRender(AbstractClientPlayer abstractClientPlayer, PoseStack stack, MultiBufferSource multiBufferSource, int i, double distance, RendererInterface renderer) {
        if (Minecraft.getInstance().getConnection() instanceof ClientListenerHook hook) {
            Component title = hook.prometheus$getHeadingTexts().get(abstractClientPlayer.getUUID());
            Heading heading = hook.prometheus$getHeadings().getOrDefault(abstractClientPlayer.getUUID(), Heading.NONE);
            if (heading == Heading.NONE) return;
            if (title == null) return;
            if (distance <= 4096.0D) {
                stack.pushPose();
                stack.translate(0.0D, (9.0F * 1.15F * 0.025F) * 1.5F, 0.0D);
                renderer.render(abstractClientPlayer, title, stack, multiBufferSource, i);
                stack.popPose();
            }
        }
    }

    @FunctionalInterface
    public interface RendererInterface {
        void render(AbstractClientPlayer player, Component component, PoseStack stack, MultiBufferSource source, int i);
    }
}
