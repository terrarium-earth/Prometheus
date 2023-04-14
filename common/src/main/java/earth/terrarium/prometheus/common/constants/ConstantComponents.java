package earth.terrarium.prometheus.common.constants;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public class ConstantComponents {

    public static final Component UNSAVED_CHANGES = Component.translatable("prometheus.ui.unsaved_changes").withStyle(ChatFormatting.RED);
    public static final Component ERROR_IN_LOGS = Component.translatable("prometheus.error.logs");
    public static final Component REMOVE = Component.translatable("prometheus.ui.remove");
    public static final Component SAVE = Component.translatable("prometheus.ui.save");
    public static final Component BACK = Component.translatable("prometheus.ui.back");
    public static final Component EDIT = Component.translatable("prometheus.ui.edit");
    public static final Component MOVE_DOWN = Component.translatable("prometheus.ui.move_down");
    public static final Component MOVE_UP = Component.translatable("prometheus.ui.move_up");
    public static final Component UNDO = Component.translatable("prometheus.ui.undo");
    public static final Component ADD = Component.translatable("prometheus.ui.add");
    public static final Component NEXT = Component.translatable("prometheus.ui.next");
    public static final Component PREV = Component.translatable("prometheus.ui.prev");

    public static final Component CANT_TP_TO_SELF = Component.translatable("prometheus.tpa.error.self_request");
    public static final Component EXPIRED = Component.translatable("prometheus.tpa.error.expired_request");
    public static final Component INVALID = Component.translatable("prometheus.tpa.error.invalid_request");
    public static final Component DENIED = Component.translatable("prometheus.tpa.error.request_denied");
    public static final Component OFFLINE = Component.translatable("prometheus.tpa.error.sender_offline");
    public static final Component TELEPORTING = Component.translatable("prometheus.tpa.teleporting");
    public static final Component REQUEST = Component.translatable("prometheus.tpa.request");

    public static final Component HOMES_UI_TITLE = Component.translatable("prometheus.locations.home");
    public static final Component HOMES_COMMAND_TITLE = Component.translatable("prometheus.locations.home.command");
    public static final Component MAX_HOMES = Component.translatable("prometheus.homes.error.max_homes");
    public static final Component HOME_DOES_NOT_EXIST = Component.translatable("prometheus.homes.error.home_does_not_exist");
    public static final Component HOME_ALREADY_EXISTS = Component.translatable("prometheus.homes.error.home_already_exists");
    public static final Component NO_HOMES = Component.translatable("prometheus.homes.error.no_homes");

    public static final Component NO_DIMENSION = Component.translatable("prometheus.location.error.cant_find_dimension");

    public static final Component CANT_EDIT_ROLE = Component.translatable("prometheus.roles.error.cant_edit_role");
    public static final Component CANT_EDIT_ROLE_IN_LIST = Component.translatable("prometheus.roles.error.cant_edit_role_in_list");
    public static final Component NOT_ALLOWED_TO_EDIT_ROLES = Component.translatable("prometheus.roles.error.not_allowed_to_edit_roles");

    public static final Component WARPS_UI_TITLE = Component.translatable("prometheus.locations.warp");
    public static final Component WARPS_COMMAND_TITLE = Component.translatable("prometheus.locations.warp.command");
    public static final Component WARP_ALREADY_EXISTS = Component.translatable("prometheus.warps.error.warp_already_exists");
    public static final Component WARP_DOES_NOT_EXIST = Component.translatable("prometheus.warps.error.warp_does_not_exist");

    public static final Component MUTED = Component.translatable("You cannot not message while muted.");

    // Option Displays
    public static final Component PERMISSIONS_TITLE = Component.translatable("prometheus.options.permissions.title");
    public static final Component PERMISSIONS_ADD = Component.translatable("prometheus.options.permissions.add");

    public static final Component COSMETIC_TITLE = Component.translatable("prometheus.options.cosmetic.title");
    public static final Component COSMETIC_ROLE_NAME = Component.translatable("prometheus.options.cosmetic.role_name");
    public static final Component COSMETIC_ROLE_ICON = Component.translatable("prometheus.options.cosmetic.role_icon");

    public static final Component HOMES_TITLE = Component.translatable("prometheus.options.homes.title");
    public static final Component HOMES_MAX = Component.translatable("prometheus.options.homes.max");

    public static final Component TPA_TITLE = Component.translatable("prometheus.options.tpa.title");
    public static final Component REQUEST_TIMEOUT = Component.translatable("prometheus.options.tpa.request_timeout");

    public static final Component NOTIFICATION_OPTION_TOOLTIP = Component.translatable("options.prometheus.notifications.tooltip");
    public static final Component SOUND_OPTION_TOOLTIP = Component.translatable("options.prometheus.sound.tooltip");
}
