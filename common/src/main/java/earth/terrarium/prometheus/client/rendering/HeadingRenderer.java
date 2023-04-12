package earth.terrarium.prometheus.client.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefullib.client.utils.RenderUtils;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.client.utils.ClientListenerHook;
import earth.terrarium.prometheus.common.handlers.heading.Heading;
import earth.terrarium.prometheus.common.handlers.heading.HeadingEntityHook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

public class HeadingRenderer {

    private static final ResourceLocation TEXTURE = new ResourceLocation(Prometheus.MOD_ID, "textures/gui/heading_icons.png");

    public static void onRenderIcon(UUID player, PoseStack stack, int x, int y) {
        ClientPacketListener listener = Minecraft.getInstance().getConnection();
        if (listener instanceof ClientListenerHook hook) {
            Heading heading = hook.prometheus$getHeadings().getOrDefault(player, Heading.NONE);
            if (heading.hasIcon()) {
                RenderUtils.bindTexture(TEXTURE);
                Gui.blit(stack, x + 1, y + 1, heading.getU(), heading.getV(), 8, 8, 256, 256);
            }
        }
    }

    public static void onRender(AbstractClientPlayer abstractClientPlayer, PoseStack stack, MultiBufferSource multiBufferSource, int i, double distance, RendererInterface renderer) {
        if (abstractClientPlayer instanceof HeadingEntityHook hook) {
            hook.prometheus$getHeadingText().ifPresent(component -> {
                if (distance <= 4096.0D) {
                    stack.pushPose();
                    stack.translate(0.0D, (9.0F * 1.15F * 0.025F) * 1.5F, 0.0D);
                    renderer.render(abstractClientPlayer, component, stack, multiBufferSource, i);
                    stack.popPose();
                }
            });
        }
    }

    @FunctionalInterface
    public interface RendererInterface {
        void render(AbstractClientPlayer player, Component component, PoseStack stack, MultiBufferSource source, int i);
    }
}
