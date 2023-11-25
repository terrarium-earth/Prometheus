package earth.terrarium.prometheus.common.commands.roles;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.teamresourceful.resourcefullib.common.utils.CommonUtils;
import earth.terrarium.prometheus.common.handlers.promotions.Promotion;
import earth.terrarium.prometheus.common.handlers.promotions.PromotionsHandler;
import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ComponentArgument;
import net.minecraft.commands.arguments.TimeArgument;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PromotionCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("promotions")
            .requires(source -> source.hasPermission(2))
            .then(add())
            .then(remove())
            .then(list())
            .then(edit())
        );
    }

    private static LiteralArgumentBuilder<CommandSourceStack> add() {
        return Commands.literal("add")
            .then(
                Commands.argument("id", StringArgumentType.word())
                    .then(Commands.argument("time", TimeArgument.time(20))
                    .executes(context -> {
                        String name = StringArgumentType.getString(context, "id");
                        PromotionsHandler.addPromotion(
                            context.getSource().getLevel(),
                            name,
                            Promotion.fromId(name, IntegerArgumentType.getInteger(context, "time"))
                        );
                        context.getSource().sendSuccess(
                            () -> CommonUtils.serverTranslatable("prometheus.promotions.created", name),
                            true
                        );
                        return 1;
                    }))
            );
    }

    private static LiteralArgumentBuilder<CommandSourceStack> remove() {
        return Commands.literal("remove")
            .then(
                Commands.argument("id", StringArgumentType.word())
                    .executes(context -> {
                        Level level = context.getSource().getLevel();
                        String name = StringArgumentType.getString(context, "id");
                        PromotionsHandler.removePromotion(level, name);
                        context.getSource().sendSuccess(
                            () -> CommonUtils.serverTranslatable("prometheus.promotions.removed", name),
                            true
                        );
                        return 1;
                    })
            );
    }

    private static LiteralArgumentBuilder<CommandSourceStack> list() {
        return Commands.literal("list")
            .executes(context -> {
                PromotionsHandler handler = PromotionsHandler.read(context.getSource().getLevel());
                for (var entry : PromotionsHandler.getPromotions(handler)) {
                    context.getSource().sendSystemMessage(entry.getSecond().name().copy()
                        .withStyle(style -> style.withHoverEvent(new HoverEvent(
                            HoverEvent.Action.SHOW_TEXT,
                            Component.literal(entry.getFirst())
                        )))
                    );
                }
                return 1;
            });
    }

    private static LiteralArgumentBuilder<CommandSourceStack> edit() {
        return Commands.literal("edit")
            .then(Commands.argument("id", StringArgumentType.word())
                .then(editName())
                .then(editRoles(true))
                .then(editRoles(false))
                .then(editTime())
            );
    }

    private static LiteralArgumentBuilder<CommandSourceStack> editName() {
        return Commands.literal("displayname")
            .then(Commands.argument("name", ComponentArgument.textComponent())
                .executes(context -> {
                    Level level = context.getSource().getLevel();
                    String id = StringArgumentType.getString(context, "id");
                    Component name = ComponentArgument.getComponent(context, "name");
                    Promotion promotion =  getPromotion(context);
                    Promotion newPromotion = new Promotion(name, promotion.time(), promotion.roles());
                    PromotionsHandler.addPromotion(level, id, newPromotion);
                    context.getSource().sendSuccess(
                        () -> CommonUtils.serverTranslatable("prometheus.promotions.edited", id),
                        true
                    );
                    return 1;
                })
            );
    }

    private static LiteralArgumentBuilder<CommandSourceStack> editRoles(boolean add) {
        return Commands.literal(add ? "addrole" : "removerole")
            .then(Commands.argument("role", UuidArgument.uuid())
                .suggests(RolesCommand.SUGGEST_ROLES)
                .executes(context -> {
                    Level level = context.getSource().getLevel();
                    String id = StringArgumentType.getString(context, "id");
                    UUID role = UuidArgument.getUuid(context, "role");
                    Promotion promotion =  getPromotion(context);
                    List<UUID> roles = new ArrayList<>(promotion.roles());
                    if (add) {
                        roles.add(role);
                    } else {
                        roles.remove(role);
                    }
                    Promotion newPromotion = new Promotion(promotion.name(), promotion.time(), roles);
                    PromotionsHandler.addPromotion(level, id, newPromotion);
                    context.getSource().sendSuccess(
                        () -> CommonUtils.serverTranslatable("prometheus.promotions.edited", id),
                        true
                    );
                    return 1;
                })
            );
    }

    private static LiteralArgumentBuilder<CommandSourceStack> editTime() {
        return Commands.literal("time")
            .then(Commands.argument("time", TimeArgument.time(10))
                .executes(context -> {
                    Level level = context.getSource().getLevel();
                    String id = StringArgumentType.getString(context, "id");
                    int time = IntegerArgumentType.getInteger(context, "time");
                    Promotion promotion =  getPromotion(context);
                    Promotion newPromotion = new Promotion(promotion.name(), time, promotion.roles());
                    PromotionsHandler.addPromotion(level, id, newPromotion);
                    context.getSource().sendSuccess(
                        () -> CommonUtils.serverTranslatable("prometheus.promotions.edited", id),
                        true
                    );
                    return 1;
                })
            );
    }

    private static Promotion getPromotion(CommandContext<CommandSourceStack> context) {
        String name = StringArgumentType.getString(context, "id");
        Promotion promotion = PromotionsHandler.getPromotion(context.getSource().getLevel(), name);
        if (promotion == null) {
            throw new CommandRuntimeException(CommonUtils.serverTranslatable("prometheus.promotions.does_not_exist", name));
        }
        return promotion;
    }
}
