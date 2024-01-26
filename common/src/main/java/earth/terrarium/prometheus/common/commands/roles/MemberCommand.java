package earth.terrarium.prometheus.common.commands.roles;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.teamresourceful.resourcefullib.common.utils.CommonUtils;
import earth.terrarium.prometheus.common.handlers.role.RoleHandler;
import earth.terrarium.prometheus.common.network.NetworkHandler;
import earth.terrarium.prometheus.common.network.messages.client.screens.ClientboundOpenMemberRolesScreenPacket;
import earth.terrarium.prometheus.common.network.messages.server.ServerboundOpenMemberRolesPacket;
import it.unimi.dsi.fastutil.objects.Object2BooleanMaps;
import net.minecraft.Optionull;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Objects;
import java.util.UUID;

public class MemberCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("member")
            .requires(source -> source.hasPermission(2))
            .then(Commands.argument("player", GameProfileArgument.gameProfile())
                .then(add())
                .then(remove())
                .executes(context -> {
                    var source = context.getSource();
                    if (source.isPlayer() && NetworkHandler.CHANNEL.canSendToPlayer(source.getPlayer(), ClientboundOpenMemberRolesScreenPacket.TYPE)) {
                        GameProfile profile = Optionull.first(GameProfileArgument.getGameProfiles(context, "player"));
                        Objects.requireNonNull(profile, "Player cannot be null");
                        ServerboundOpenMemberRolesPacket packet = new ServerboundOpenMemberRolesPacket(profile.getId());
                        ServerPlayer player = source.getPlayerOrException();
                        ServerboundOpenMemberRolesPacket.TYPE.handle(packet).accept(player);
                    } else {
                        context.getSource().sendSystemMessage(Component.literal("You cannot use this command without the mod on your client."));
                    }
                    return 1;
                })
            )
        );
    }

    private static LiteralArgumentBuilder<CommandSourceStack> add() {
        return Commands.literal("addrole")
            .then(
                Commands.argument("id", UuidArgument.uuid())
                    .suggests(RolesCommand.SUGGEST_ROLES)
                    .executes(context -> {
                        GameProfile profile = Optionull.first(GameProfileArgument.getGameProfiles(context, "player"));
                        Objects.requireNonNull(profile, "Player cannot be null");
                        UUID uuid = UuidArgument.getUuid(context, "id");
                        RoleHandler.changeRoles(
                            context.getSource().getLevel(),
                            profile.getId(),
                            Object2BooleanMaps.singleton(uuid, true)
                        );
                        context.getSource().sendSuccess(
                            () -> CommonUtils.serverTranslatable("prometheus.roles.member.added", profile.getName(), uuid.toString()),
                            true
                        );
                        return 1;
                    })
            );
    }

    private static LiteralArgumentBuilder<CommandSourceStack> remove() {
        return Commands.literal("removerole")
            .then(
                Commands.argument("id", UuidArgument.uuid())
                    .suggests(RolesCommand.SUGGEST_ROLES)
                    .executes(context -> {
                        GameProfile profile = Optionull.first(GameProfileArgument.getGameProfiles(context, "player"));
                        Objects.requireNonNull(profile, "Player cannot be null");
                        UUID uuid = UuidArgument.getUuid(context, "id");
                        RoleHandler.changeRoles(
                            context.getSource().getLevel(),
                            profile.getId(),
                            Object2BooleanMaps.singleton(uuid, false)
                        );
                        context.getSource().sendSuccess(
                            () -> CommonUtils.serverTranslatable("prometheus.roles.member.removed", profile.getName(), uuid.toString()),
                            true
                        );
                        return 1;
                    })
            );
    }
}
