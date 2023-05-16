package earth.terrarium.prometheus.common.commands.utilities;

import com.mojang.brigadier.CommandDispatcher;
import earth.terrarium.prometheus.api.roles.RoleApi;
import earth.terrarium.prometheus.common.constants.ConstantComponents;
import earth.terrarium.prometheus.common.handlers.cooldowns.CooldownHandler;
import earth.terrarium.prometheus.common.roles.TeleportOptions;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.levelgen.Heightmap;

public class RtpCommand {

    private static final int MAX_TRIES = 10;

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("rtp")
            .executes(context -> {
                ServerPlayer player = context.getSource().getPlayerOrException();
                if (player.level.dimensionType().hasCeiling()) {
                    context.getSource().sendFailure(ConstantComponents.FAILED_WITH_CEILING);
                    return 0;
                }
                if (CooldownHandler.hasCooldown(player, "rtp")) {
                    int timeLeft = (int) ((CooldownHandler.getCooldown(player, "rtp") - System.currentTimeMillis()) / 1000);
                    context.getSource().sendFailure(Component.translatable("prometheus.rtp.failed_cooldown", timeLeft));
                    return 0;
                }
                TeleportOptions options = RoleApi.API.getNonNullOption(player, TeleportOptions.SERIALIZER);
                if (tp(player, options.rtpDistance(), 0)) {
                    CooldownHandler.setCooldown(player, "rtp", options.rtpCooldown());
                    return 1;
                }
                return 0;
            }));
    }

    public static boolean tp(ServerPlayer player, int distance, int tries) {
        if (tries > MAX_TRIES) {
            player.sendSystemMessage(ConstantComponents.FAILED_MAX_TRIES);
            return false;
        }
        Level level = player.level;

        final int min = distance / 4;
        final int max = distance - min;

        int x = min + player.getRandom().nextInt(-max, max) + player.getBlockX();
        int z = min + player.getRandom().nextInt(-max, max) + player.getBlockZ();

        if (!level.getWorldBorder().isWithinBounds(x, z) || level.getBiome(new BlockPos(x, level.getSeaLevel(), z)).is(BiomeTags.IS_OCEAN)) {
            return tp(player, distance, tries + 1);
        }

        level.getChunk(SectionPos.blockToSectionCoord(x), SectionPos.blockToSectionCoord(z), ChunkStatus.HEIGHTMAPS);

        BlockPos pos = new BlockPos(x, level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z), z);

        if (!isSafe(player, pos)) {
            return tp(player, distance, tries + 1);
        }

        player.teleportTo(pos.getX() + 0.5, pos.getY() + 0.2, pos.getZ() + 0.5);
        player.sendSystemMessage(ConstantComponents.TELEPORTED);
        return true;
    }

    private static boolean isSafe(Player player, BlockPos pos) {
        return player.level.getBlockState(pos).isAir() &&
            player.level.getBlockState(pos.above()).isAir() &&
            player.level.getBlockState(pos.above(2)).isAir()
            && player.level.getBlockState(pos.below()).entityCanStandOn(player.level, pos.below(), player);
    }
}
