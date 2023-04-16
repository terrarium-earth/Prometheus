package earth.terrarium.prometheus.mixin.client;

import earth.terrarium.prometheus.client.handlers.ClientOptionHandler;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.ChatOptionsScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.SimpleOptionsSubScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(SimpleOptionsSubScreen.class)
public class ChatOptionsScreenMixin {

    @Shadow
    @Final
    @Mutable
    protected OptionInstance<?>[] smallOptions;

    @Inject(
        method = "<init>",
        at = @At("RETURN")
    )
    @SuppressWarnings("ConstantValue")
    private void prometheus$init(Screen screen, Options options, Component component, OptionInstance<?>[] optionInstances, CallbackInfo ci) {
        Class<?> clazz = this.getClass();
        if (clazz == ChatOptionsScreen.class) {
            List<OptionInstance<?>> optionsList = ClientOptionHandler.getChatOptions();
            var smallOptions = new OptionInstance[this.smallOptions.length + optionsList.size()];
            for (int i = 0; i < optionsList.size(); i++) {
                smallOptions[this.smallOptions.length + i] = optionsList.get(i);
            }
            System.arraycopy(this.smallOptions, 0, smallOptions, 0, this.smallOptions.length);
            this.smallOptions = smallOptions;
        }
    }
}
