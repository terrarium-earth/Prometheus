package earth.terrarium.prometheus.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import earth.terrarium.prometheus.client.rendering.HeadingRenderer;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererMixin extends EntityRenderer<AbstractClientPlayer> {

    protected PlayerRendererMixin(EntityRendererProvider.Context context) {
        super(context);
    }

    @Inject(
        method = "renderNameTag(Lnet/minecraft/client/player/AbstractClientPlayer;Lnet/minecraft/network/chat/Component;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IF)V",
        at = @At("TAIL")
    )
    public void onRenderNameTag(AbstractClientPlayer player, Component name, PoseStack stack, MultiBufferSource source, int packedLight, float partialTick, CallbackInfo ci) {
        double d = this.entityRenderDispatcher.distanceToSqr(player);
        HeadingRenderer.onRender(player, stack, source, packedLight, partialTick, d, super::renderNameTag);
    }
}
