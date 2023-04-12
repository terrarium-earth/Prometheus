package earth.terrarium.prometheus.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.vertex.PoseStack;
import earth.terrarium.prometheus.client.rendering.HeadingRenderer;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerTabOverlay.class)
public class PlayerTabOverlayMixin {

    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/components/PlayerFaceRenderer;draw(Lcom/mojang/blaze3d/vertex/PoseStack;IIIZZ)V",
                    shift = At.Shift.AFTER
            )
    )
    private void prometheus$onRenderFace(
            PoseStack poseStack, int i, Scoreboard scoreboard, Objective objective, CallbackInfo ci,
            @Local(ordinal = 15) int x, @Local(ordinal = 16) int y, @Local GameProfile profile
    ) {
        HeadingRenderer.onRenderIcon(profile.getId(), poseStack, x, y);
    }
}
