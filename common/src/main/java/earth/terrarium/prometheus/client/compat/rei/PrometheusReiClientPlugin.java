package earth.terrarium.prometheus.client.compat.rei;

import me.shedaniel.rei.api.client.favorites.FavoriteEntryType;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import net.minecraft.network.chat.Component;

@SuppressWarnings("UnstableApiUsage")
public class PrometheusReiClientPlugin implements REIClientPlugin {

    @Override
    public void registerFavorites(FavoriteEntryType.Registry registry) {
        registry.register(HomeFavoriteEntry.ID, HomeFavoriteEntry.Type.INSTANCE);
        registry.getOrCrateSection(Component.translatable("rei.sections.project_odyssey"))
            .add(new HomeFavoriteEntry());
    }
}