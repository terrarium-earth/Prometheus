package earth.terrarium.prometheus.common.commands.utilities;

import com.mojang.brigadier.CommandDispatcher;
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

    private static final int MAX_DISTANCE = 3000;
    private static final int MAX_TRIES = 10;

    private static final Component TELEPORTED = Component.translatable("commands.rtp.success");
    private static final Component FAILED_WITH_CEILING = Component.translatable("prometheus.rtp.failed_with_ceiling");
    private static final Component FAILED_MAX_TRIES = Component.translatable("prometheus.rtp.failed_max_tries");

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("rtp")
                .executes(context -> {
                    tp(context.getSource().getPlayerOrException(), 0);
                    return 1;
                }));
    }

    public static void tp(ServerPlayer player, int tries) {
        if (tries > MAX_TRIES) {
            player.sendSystemMessage(FAILED_MAX_TRIES);
            return;
        }
        Level level = player.level;
        if (level.dimensionType().hasCeiling()) {
            player.sendSystemMessage(FAILED_WITH_CEILING);
            return;
        }

        final int min = MAX_DISTANCE / 4;
        final int max = MAX_DISTANCE - min;

        int x = min + player.getRandom().nextInt(-max, max) + player.getBlockX();
        int z = min + player.getRandom().nextInt(-max, max) + player.getBlockZ();

        if (!level.getWorldBorder().isWithinBounds(x, z) || level.getBiome(new BlockPos(x, level.getSeaLevel(), z)).is(BiomeTags.IS_OCEAN)) {
            tp(player, tries + 1);
            return;
        }

        level.getChunk(SectionPos.blockToSectionCoord(x), SectionPos.blockToSectionCoord(z), ChunkStatus.HEIGHTMAPS);

        BlockPos pos = new BlockPos(x, level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z), z);

        if (!isSafe(player, pos)) {
            tp(player, tries + 1);
            return;
        }

        player.teleportTo(pos.getX(), pos.getY(), pos.getZ());
        player.sendSystemMessage(TELEPORTED);
    }

    private static boolean isSafe(Player player, BlockPos pos) {
        return player.level.getBlockState(pos).isAir() &&
                player.level.getBlockState(pos.above()).isAir() &&
                player.level.getBlockState(pos.above(2)).isAir()
                && player.level.getBlockState(pos.below()).entityCanStandOn(player.level, pos.below(), player);
    }
}
