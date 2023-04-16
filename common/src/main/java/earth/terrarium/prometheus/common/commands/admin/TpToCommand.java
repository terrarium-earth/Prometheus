package earth.terrarium.prometheus.common.commands.admin;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class TpToCommand {

    private static final SuggestionProvider<CommandSourceStack> SUGGESTION_PROVIDER = (context, builder) -> {
        SharedSuggestionProvider.suggestResource(context.getSource().getServer().levelKeys().stream().map(ResourceKey::location), builder);
        return builder.buildFuture();
    };

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        var command = dispatcher.register(Commands.literal("tpto")
            .requires(source -> source.hasPermission(2))
            .then(Commands.argument("location", ResourceLocationArgument.id())
                .suggests(SUGGESTION_PROVIDER)
                .then(Commands.argument("pos", BlockPosArgument.blockPos()).executes(context -> {
                    BlockPos pos = BlockPosArgument.getBlockPos(context, "pos");
                    teleport(context, context.getSource().getPlayerOrException(), pos);
                    return 1;
                }))
                .executes(context -> {
                    final ServerPlayer player = context.getSource().getPlayerOrException();
                    teleport(context, player, player.blockPosition());
                    return 1;
                })
            )
        );
        dispatcher.register(Commands.literal("teleportto")
            .requires(source -> source.hasPermission(2))
            .redirect(command)
        );
    }

    private static void teleport(CommandContext<CommandSourceStack> context, ServerPlayer player, BlockPos pos) {
        ResourceLocation location = ResourceLocationArgument.getId(context, "location");
        ServerLevel level = context.getSource().getServer().getLevel(ResourceKey.create(Registries.DIMENSION, location));
        if (level != null) {
            player.teleportTo(level, pos.getX(), pos.getY(), pos.getZ(), player.getYRot(), player.getXRot());
        }
    }
}
