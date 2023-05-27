package earth.terrarium.prometheus.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.authlib.GameProfile;
import earth.terrarium.prometheus.client.rendering.HeadingRenderer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;
import org.jetbrains.annotations.Nullable;
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
            target = "Lnet/minecraft/client/gui/components/PlayerFaceRenderer;draw(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/resources/ResourceLocation;IIIZZ)V",
            shift = At.Shift.AFTER
        )
    )
    private void prometheus$onRenderFace(
        GuiGraphics guiGraphics, int i, Scoreboard scoreboard, @Nullable Objective objective, CallbackInfo ci,
        @Local(ordinal = 15) int x, @Local(ordinal = 16) int y, @Local GameProfile profile
    ) {
        HeadingRenderer.onRenderIcon(profile.getId(), guiGraphics, x, y);
    }
}
