package earth.terrarium.prometheus.mixin.client;

import earth.terrarium.prometheus.client.handlers.stuck.StuckRenderer;
import earth.terrarium.prometheus.client.handlers.stuck.StuckScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public abstract class ScreenMixin implements StuckScreen {

    @Shadow(aliases = { "m_142416_" })
    protected abstract <T extends GuiEventListener & Renderable & NarratableEntry> T addRenderableWidget(T widget);

    @Inject(method = "init()V", at = @At("RETURN"))
    private void prometheus$init(CallbackInfo ci) {
        for (Button button : StuckRenderer.render((Screen) (Object) this, this.prometheus$isStuck())) {
            this.addRenderableWidget(button);
        }
    }

    // StuckScreen implementation

    @Unique
    private boolean prometheus$stuck = false;

    @Override
    public boolean prometheus$isStuck() {
        return prometheus$stuck;
    }

    @Override
    public void prometheus$setStuck(boolean stuck) {
        prometheus$stuck = stuck;
    }
}
