package earth.terrarium.prometheus.mixin.client;

import earth.terrarium.prometheus.client.handlers.ClientOptionHandler;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.options.AccessibilityOptionsScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(AccessibilityOptionsScreen.class)
public class AccessibilityOptionsScreenMixin {

    @Inject(method = "options", at = @At("RETURN"), cancellable = true)
    private static void prometheus$addAccessibilityOptions(Options options, CallbackInfoReturnable<OptionInstance<?>[]> cir) {
        List<OptionInstance<?>> optionsList = ClientOptionHandler.getAccessibilityOptions();
        OptionInstance<?>[] optionInstances = cir.getReturnValue();
        OptionInstance<?>[] newOptionInstances = new OptionInstance[optionInstances.length + optionsList.size()];
        for (int i = 0; i < optionsList.size(); i++) newOptionInstances[optionInstances.length + i] = optionsList.get(i);
        System.arraycopy(optionInstances, 0, newOptionInstances, 0, optionInstances.length);
        cir.setReturnValue(newOptionInstances);
    }
}
