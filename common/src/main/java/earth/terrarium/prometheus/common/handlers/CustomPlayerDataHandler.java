package earth.terrarium.prometheus.common.handlers;

import com.mojang.serialization.DataResult;
import net.minecraft.nbt.CompoundTag;

import java.util.UUID;
import java.util.function.Consumer;

public interface CustomPlayerDataHandler {

    DataResult<CompoundTag> prometheus$edit(UUID uuid, Consumer<CompoundTag> editor);
}
