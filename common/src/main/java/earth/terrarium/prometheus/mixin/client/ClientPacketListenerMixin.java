package earth.terrarium.prometheus.mixin.client;

import earth.terrarium.prometheus.client.handlers.stuck.StuckRenderer;
import earth.terrarium.prometheus.client.utils.ClientListenerHook;
import earth.terrarium.prometheus.common.handlers.heading.Heading;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin implements ClientListenerHook {

    // Headings
    @Unique
    private final Map<UUID, Heading> prometheus$headings = new HashMap<>();

    @Unique
    private final Map<UUID, Component> prometheus$headingTexts = new HashMap<>();

    @Override
    public Map<UUID, Heading> prometheus$getHeadings() {
        return prometheus$headings;
    }

    @Override
    public Map<UUID, Component> prometheus$getHeadingTexts() {
        return prometheus$headingTexts;
    }

    @Override
    public void prometheus$setHeading(UUID uuid, Heading heading) {
        prometheus$headings.put(uuid, heading);
    }

    @Override
    public void prometheus$setHeadingText(UUID uuid, Component text) {
        if (text == null) {
            prometheus$headingTexts.remove(uuid);
            return;
        }
        prometheus$headingTexts.put(uuid, text);
    }

    // On Receive Login

    @Inject(method = "handleLogin", at = @At("HEAD"))
    private void prometheus$handleLoginStart(CallbackInfo ci) {
        StuckRenderer.shouldRender = true;
    }

    @Inject(method = "handleLogin", at = @At("RETURN"))
    private void prometheus$handleLoginEnd(CallbackInfo ci) {
        StuckRenderer.shouldRender = false;
    }
}
