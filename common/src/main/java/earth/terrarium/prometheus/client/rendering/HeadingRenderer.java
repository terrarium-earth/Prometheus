package earth.terrarium.prometheus.client.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import earth.terrarium.prometheus.common.handlers.heading.HeadingEntityHook;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;

public class HeadingRenderer {

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
