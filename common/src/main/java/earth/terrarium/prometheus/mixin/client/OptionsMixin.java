package earth.terrarium.prometheus.mixin.client;

import earth.terrarium.prometheus.client.handlers.ClientOptionHandler;
import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Options.class)
public class OptionsMixin {

    @Inject(method = "load", at = @At("HEAD"))
    private void prometheus$load(CallbackInfo ci) {
        ClientOptionHandler.onLoad();
    }

    @Inject(method = "processOptions", at = @At("HEAD"))
    private void prometheus$processOptions(Options.FieldAccess fieldAccess, CallbackInfo ci) {
        ClientOptionHandler.onParse(fieldAccess);
    }
}
