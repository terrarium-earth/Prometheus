package earth.terrarium.prometheus.common.handlers.commands;

import earth.terrarium.prometheus.common.handlers.base.Handler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DynamicCommandHandler extends Handler {

    private static final DynamicCommandHandler CLIENT_SIDE = new DynamicCommandHandler();

    private final Map<String, List<String>> commands = new HashMap<>();

    public static DynamicCommandHandler read(Level level) {
        return read(level, CLIENT_SIDE, DynamicCommandHandler::new, "prometheus_commands");
    }

    public static void removeCommand(ServerLevel level, String name) {
        handle(level, DynamicCommandHandler::read, handler -> handler.commands.remove(name));
    }

    public static void putCommand(ServerLevel level, String name, List<String> command) {
        handle(level, DynamicCommandHandler::read, handler -> handler.commands.put(name, command));
    }

    public static Collection<String> getCommands(ServerLevel level) {
        return read(level).commands.keySet();
    }

    public static List<String> getCommand(ServerLevel level, String name) {
        return read(level).commands.getOrDefault(name, List.of());
    }

    @Override
    public void loadData(CompoundTag tag) {
        tag.getAllKeys().forEach(key -> {
            List<String> command = tag.getList(key, Tag.TAG_STRING).stream().map(Tag::getAsString).toList();
            this.commands.put(key, command);
        });
    }

    @Override
    public void saveData(CompoundTag tag) {
        this.commands.forEach((key, value) -> {
            ListTag listTag = new ListTag();
            value.stream().map(StringTag::valueOf).forEach(listTag::add);
            tag.put(key, listTag);
        });
    }
}
