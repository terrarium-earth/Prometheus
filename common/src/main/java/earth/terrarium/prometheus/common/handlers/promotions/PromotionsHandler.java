package earth.terrarium.prometheus.common.handlers.promotions;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefullib.common.codecs.CodecExtras;
import com.teamresourceful.resourcefullib.common.utils.CommonUtils;
import com.teamresourceful.resourcefullib.common.utils.files.CodecSavedData;
import net.minecraft.core.UUIDUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;

import java.util.*;

public record PromotionsHandler(
    Map<String, Promotion> promotions,
    Map<UUID, Set<String>> playerPromotions
) {

    private static final Codec<PromotionsHandler> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.unboundedMap(Codec.STRING, Promotion.CODEC).fieldOf("promotions").forGetter(PromotionsHandler::promotions),
        Codec.unboundedMap(UUIDUtil.STRING_CODEC, CodecExtras.set(Codec.STRING)).fieldOf("playerPromotions").forGetter(PromotionsHandler::playerPromotions)
    ).apply(instance, (p, pp) -> new PromotionsHandler(new HashMap<>(p), new HashMap<>(pp))));

    private static final CodecSavedData.Factory<PromotionsHandler> DATA = CodecSavedData
        .create(CODEC, "prometheus_promotions")
        .defaultValue(PromotionsHandler::new)
        .global()
        .clean();

    private PromotionsHandler() {
        this(new HashMap<>(), new HashMap<>());
    }

    public static void removePromotion(ServerLevel level, String id) {
        var data = DATA.create(level);
        var promotions = data.get();
        promotions.promotions.remove(id);
        promotions.playerPromotions.values().forEach(set -> set.remove(id));
        data.setDirty();
    }

    public static Promotion getPromotion(ServerLevel level, String id) {
        return DATA.create(level).get().promotions.get(id);
    }

    public static List<Pair<String, Promotion>> getPromotions(ServerLevel level) {
        var handler = DATA.create(level).get();
        return handler.promotions.entrySet().stream()
            .map(entry -> Pair.of(entry.getKey(), entry.getValue()))
            .toList();
    }

    public static void addPromotion(ServerLevel level, String id, Promotion promotion) {
        var data = DATA.create(level);
        data.get().promotions.put(id, promotion);
        data.setDirty();
    }

    public static void runChecks(MinecraftServer server) {
        var data = DATA.create(server.overworld());
        PromotionsHandler handler = data.get();
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            int time = player.getStats().getValue(Stats.CUSTOM.get(Stats.PLAY_TIME));
            handler.promotions.forEach((id, promotion) -> {
                if (time >= promotion.time()) {
                    if (!handler.playerPromotions.containsKey(player.getUUID())) {
                        handler.playerPromotions.computeIfAbsent(player.getUUID(), uuid -> new HashSet<>()).add(id);
                        promotion.run(player);
                        player.sendSystemMessage(CommonUtils.serverTranslatable("prometheus.promotions.promoted", promotion.name()));
                        data.setDirty();
                    }
                }
            });
        }
    }
}
