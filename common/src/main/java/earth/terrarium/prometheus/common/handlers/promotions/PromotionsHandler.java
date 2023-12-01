package earth.terrarium.prometheus.common.handlers.promotions;

import com.mojang.datafixers.util.Pair;
import com.teamresourceful.resourcefullib.common.utils.CommonUtils;
import com.teamresourceful.resourcefullib.common.utils.SaveHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class PromotionsHandler extends SaveHandler {

    private static final PromotionsHandler CLIENT_SIDE = new PromotionsHandler();

    private final Map<String, Promotion> promotions = new HashMap<>();
    private final Map<UUID, Set<String>> playerPromotions = new HashMap<>();

    public static PromotionsHandler read(Level level) {
        return read(level, HandlerType.create(CLIENT_SIDE, PromotionsHandler::new), "prometheus_promotions");
    }

    public static void removePromotion(Level level, String id) {
        PromotionsHandler handler = read(level);
        handler.promotions.remove(id);
        handler.playerPromotions.values().forEach(set -> set.remove(id));
        handler.setDirty();
    }

    public static Promotion getPromotion(Level level, String id) {
        return read(level).promotions.get(id);
    }

    public static List<Pair<String, Promotion>> getPromotions(PromotionsHandler handler) {
        return handler.promotions.entrySet().stream()
            .map(entry -> Pair.of(entry.getKey(), entry.getValue()))
            .toList();
    }

    public static void addPromotion(Level level, String id, Promotion promotion) {
        PromotionsHandler handler = read(level);
        handler.promotions.put(id, promotion);
        handler.setDirty();
    }

    public static void runChecks(MinecraftServer server) {
        PromotionsHandler handler = read(server.overworld());
        server.getPlayerList().getPlayers().forEach(player -> runCheck(handler, player));
    }

    private static void runCheck(PromotionsHandler handler, ServerPlayer player) {
        int time = player.getStats().getValue(Stats.CUSTOM.get(Stats.PLAY_TIME));
        handler.promotions.forEach((id, promotion) -> {
            if (time >= promotion.time()) {
                if (!handler.playerPromotions.containsKey(player.getUUID())) {
                    handler.playerPromotions.computeIfAbsent(player.getUUID(), uuid -> new HashSet<>()).add(id);
                    promotion.run(player);
                    player.sendSystemMessage(CommonUtils.serverTranslatable("prometheus.promotions.promoted", promotion.name()));
                    handler.setDirty();
                }
            }
        });
    }

    @Override
    public void saveData(@NotNull CompoundTag tag) {
        promotions.forEach((key, value) -> tag.put(key, value.toTag()));
        playerPromotions.forEach((key, value) -> {
            ListTag list = new ListTag();
            value.forEach(id -> list.add(StringTag.valueOf(id)));
            tag.put(key.toString(), list);
        });
    }

    @Override
    public void loadData(CompoundTag tag) {
        CompoundTag promotions = tag.getCompound("promotions");
        CompoundTag playerPromotions = tag.getCompound("playerPromotions");
        promotions.getAllKeys()
            .forEach(key -> this.promotions.put(key, Promotion.fromTag(tag.getCompound(key))));
        playerPromotions.getAllKeys()
            .forEach(key -> {
                Set<String> set = new HashSet<>();
                playerPromotions.getList(key, 8).forEach(value -> set.add(value.getAsString()));
                this.playerPromotions.put(UUID.fromString(key), set);
            });
    }
}
