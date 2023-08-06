package earth.terrarium.prometheus.common.constants;

import com.teamresourceful.resourcefullib.common.utils.CommonUtils;
import earth.terrarium.prometheus.common.utils.DeferredComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public class ConstantComponents {

    public static final Component UNSAVED_CHANGES = DeferredComponent.of(() -> CommonUtils.serverTranslatable("prometheus.ui.unsaved_changes").copy().withStyle(ChatFormatting.RED));
    public static final Component ERROR_IN_LOGS = DeferredComponent.of(() -> CommonUtils.serverTranslatable("prometheus.error.logs"));
    public static final Component REMOVE = DeferredComponent.of(() -> CommonUtils.serverTranslatable("prometheus.ui.remove"));
    public static final Component SAVE = DeferredComponent.of(() -> CommonUtils.serverTranslatable("prometheus.ui.save"));
    public static final Component BACK = DeferredComponent.of(() -> CommonUtils.serverTranslatable("prometheus.ui.back"));
    public static final Component EDIT = DeferredComponent.of(() -> CommonUtils.serverTranslatable("prometheus.ui.edit"));
    public static final Component MOVE_DOWN = DeferredComponent.of(() -> CommonUtils.serverTranslatable("prometheus.ui.move_down"));
    public static final Component MOVE_UP = DeferredComponent.of(() -> CommonUtils.serverTranslatable("prometheus.ui.move_up"));
    public static final Component UNDO = DeferredComponent.of(() -> CommonUtils.serverTranslatable("prometheus.ui.undo"));
    public static final Component ADD = DeferredComponent.of(() -> CommonUtils.serverTranslatable("prometheus.ui.add"));
    public static final Component NEXT = DeferredComponent.of(() -> CommonUtils.serverTranslatable("prometheus.ui.next"));
    public static final Component PREV = DeferredComponent.of(() -> CommonUtils.serverTranslatable("prometheus.ui.prev"));

    public static final Component CANT_TP_TO_SELF = DeferredComponent.of(() -> CommonUtils.serverTranslatable("prometheus.tpa.error.self_request"));
    public static final Component EXPIRED = DeferredComponent.of(() -> CommonUtils.serverTranslatable("prometheus.tpa.error.expired_request"));
    public static final Component INVALID = DeferredComponent.of(() -> CommonUtils.serverTranslatable("prometheus.tpa.error.invalid_request"));
    public static final Component DENIED = DeferredComponent.of(() -> CommonUtils.serverTranslatable("prometheus.tpa.error.request_denied"));
    public static final Component OFFLINE = DeferredComponent.of(() -> CommonUtils.serverTranslatable("prometheus.tpa.error.sender_offline"));
    public static final Component TELEPORTING = DeferredComponent.of(() -> CommonUtils.serverTranslatable("prometheus.tpa.teleporting"));
    public static final Component REQUEST = DeferredComponent.of(() -> CommonUtils.serverTranslatable("prometheus.tpa.request"));

    public static final Component HOMES_UI_TITLE = DeferredComponent.of(() -> CommonUtils.serverTranslatable("prometheus.locations.home"));
    public static final Component HOMES_COMMAND_TITLE = DeferredComponent.of(() -> CommonUtils.serverTranslatable("prometheus.locations.home.command"));
    public static final Component MAX_HOMES = DeferredComponent.of(() -> CommonUtils.serverTranslatable("prometheus.homes.error.max_homes"));
    public static final Component HOME_DOES_NOT_EXIST = DeferredComponent.of(() -> CommonUtils.serverTranslatable("prometheus.homes.error.home_does_not_exist"));
    public static final Component HOME_ALREADY_EXISTS = DeferredComponent.of(() -> CommonUtils.serverTranslatable("prometheus.homes.error.home_already_exists"));
    public static final Component NO_HOMES = DeferredComponent.of(() -> CommonUtils.serverTranslatable("prometheus.homes.error.no_homes"));
    public static final Component MULTIPLE_HOMES = DeferredComponent.of(() -> CommonUtils.serverTranslatable("prometheus.homes.error.multiple_homes"));

    public static final Component NO_DIMENSION = DeferredComponent.of(() -> CommonUtils.serverTranslatable("prometheus.location.error.cant_find_dimension"));

    public static final Component CANT_EDIT_ROLE = DeferredComponent.of(() -> CommonUtils.serverTranslatable("prometheus.roles.error.cant_edit_role"));
    public static final Component CANT_EDIT_ROLE_IN_LIST = DeferredComponent.of(() -> CommonUtils.serverTranslatable("prometheus.roles.error.cant_edit_role_in_list"));
    public static final Component NOT_ALLOWED_TO_EDIT_ROLES = DeferredComponent.of(() -> CommonUtils.serverTranslatable("prometheus.roles.error.not_allowed_to_edit_roles"));

    public static final Component WARPS_UI_TITLE = DeferredComponent.of(() -> CommonUtils.serverTranslatable("prometheus.locations.warp"));
    public static final Component WARPS_COMMAND_TITLE = DeferredComponent.of(() -> CommonUtils.serverTranslatable("prometheus.locations.warp.command"));
    public static final Component WARP_ALREADY_EXISTS = DeferredComponent.of(() -> CommonUtils.serverTranslatable("prometheus.warps.error.warp_already_exists"));
    public static final Component WARP_DOES_NOT_EXIST = DeferredComponent.of(() -> CommonUtils.serverTranslatable("prometheus.warps.error.warp_does_not_exist"));
    public static final Component NO_WARPS = DeferredComponent.of(() -> CommonUtils.serverTranslatable("prometheus.warps.error.no_warps"));

    public static final Component MUTED = DeferredComponent.of(() -> CommonUtils.serverTranslatable("prometheus.chat.error.muted"));

    // Option Displays
    public static final Component PERMISSIONS_TITLE = DeferredComponent.of(() -> CommonUtils.serverTranslatable("prometheus.options.permissions.title"));
    public static final Component PERMISSIONS_ADD = DeferredComponent.of(() -> CommonUtils.serverTranslatable("prometheus.options.permissions.add"));

    public static final Component COSMETIC_TITLE = DeferredComponent.of(() -> CommonUtils.serverTranslatable("prometheus.options.cosmetic.title"));
    public static final Component COSMETIC_ROLE_NAME = DeferredComponent.of(() -> CommonUtils.serverTranslatable("prometheus.options.cosmetic.role_name"));
    public static final Component COSMETIC_ROLE_ICON = DeferredComponent.of(() -> CommonUtils.serverTranslatable("prometheus.options.cosmetic.role_icon"));

    public static final Component HOMES_TITLE = DeferredComponent.of(() -> CommonUtils.serverTranslatable("prometheus.options.homes.title"));
    public static final Component HOMES_MAX = DeferredComponent.of(() -> CommonUtils.serverTranslatable("prometheus.options.homes.max"));

    public static final Component TELEPORT_TITLE = DeferredComponent.of(() -> CommonUtils.serverTranslatable("prometheus.options.teleport.title"));
    public static final Component REQUEST_TIMEOUT = DeferredComponent.of(() -> CommonUtils.serverTranslatable("prometheus.options.teleport.request_timeout"));
    public static final Component REQUEST_TIMEOUT_TOOLTIP = DeferredComponent.of(() -> CommonUtils.serverTranslatable("prometheus.options.teleport.request_timeout.tooltip"));
    public static final Component RTP_COOLDOWN = DeferredComponent.of(() -> CommonUtils.serverTranslatable("prometheus.options.teleport.rtp_cooldown"));
    public static final Component RTP_COOLDOWN_TOOLTIP = DeferredComponent.of(() -> CommonUtils.serverTranslatable("prometheus.options.teleport.rtp_cooldown.tooltip"));
    public static final Component RTP_DISTANCE = DeferredComponent.of(() -> CommonUtils.serverTranslatable("prometheus.options.teleport.rtp_distance"));
    public static final Component RTP_DISTANCE_TOOLTIP = DeferredComponent.of(() -> CommonUtils.serverTranslatable("prometheus.options.teleport.rtp_distance.tooltip"));

    public static final Component NOTIFICATION_OPTION_TOOLTIP = DeferredComponent.of(() -> CommonUtils.serverTranslatable("options.prometheus.notifications.tooltip"));
    public static final Component SOUND_OPTION_TOOLTIP = DeferredComponent.of(() -> CommonUtils.serverTranslatable("options.prometheus.sound.tooltip"));

    public static final Component CANT_GIVE_ROLE = DeferredComponent.of(() -> CommonUtils.serverTranslatable("prometheus.roles.error.cant_give_role"));

    public static final Component NO_PERMISSION = DeferredComponent.of(() -> CommonUtils.serverTranslatable("prometheus.run.no_permission"));
    public static final Component TELEPORTED = DeferredComponent.of(() -> CommonUtils.serverTranslatable("prometheus.rtp.success"));
    public static final Component FAILED_WITH_CEILING = DeferredComponent.of(() -> CommonUtils.serverTranslatable("prometheus.rtp.failed_with_ceiling"));
    public static final Component FAILED_MAX_TRIES = DeferredComponent.of(() -> CommonUtils.serverTranslatable("prometheus.rtp.failed_max_tries"));
    public static final Component MEMBER_ERROR = DeferredComponent.of(() -> CommonUtils.serverTranslatable("prometheus.roles.member.error"));
    public static final Component CLICK_EDIT = DeferredComponent.of(() -> CommonUtils.serverTranslatable("prometheus.commands.click_edit"));
    public static final Component CANT_FIND_LOCATION = DeferredComponent.of(() -> CommonUtils.serverTranslatable("prometheus.spawn.cant_find_location"));
}
