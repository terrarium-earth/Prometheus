package earth.terrarium.prometheus.common.handlers.base;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class Handler extends SavedData {

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag tag) {
        this.saveData(tag);
        return tag;
    }

    public abstract void loadData(CompoundTag tag);

    public abstract void saveData(CompoundTag tag);

    @SuppressWarnings("resource")
    public static <T extends Handler> T read(Level level, T client, Supplier<T> creator, String id) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return client;
        }
        return read(serverLevel.getServer().overworld().getDataStorage(), creator, id);
    }

    public static <T extends Handler> T read(DimensionDataStorage storage, Supplier<T> creator, String id) {
        return storage.computeIfAbsent(tag -> {
            T handler = creator.get();
            handler.loadData(tag);
            return handler;
        }, creator, id);
    }

    public static <T extends Handler> void handle(Level level, Function<Level, T> getter, Consumer<T> operation) {
        T handler = getter.apply(level);
        operation.accept(handler);
        handler.setDirty();
    }

}
