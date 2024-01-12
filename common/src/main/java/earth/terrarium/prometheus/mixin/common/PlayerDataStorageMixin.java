package earth.terrarium.prometheus.mixin.common;

import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.DataResult;
import earth.terrarium.prometheus.common.handlers.CustomPlayerDataHandler;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.storage.PlayerDataStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.function.Consumer;

@Mixin(PlayerDataStorage.class)
public class PlayerDataStorageMixin implements CustomPlayerDataHandler {

    @Shadow
    @Final
    private File playerDir;

    @Shadow
    @Final
    protected DataFixer fixerUpper;

    @Override
    public DataResult<CompoundTag> prometheus$edit(UUID uuid, Consumer<CompoundTag> editor) {
        CompoundTag tag;

        try {
            File file = new File(this.playerDir, uuid.toString() + ".dat");
            if (file.exists() && file.isFile()) {
                tag = NbtIo.readCompressed(file.toPath(), NbtAccounter.unlimitedHeap());
            } else {
                return DataResult.error(() -> "Player data file for " + uuid + " does not exist");
            }
        } catch (Exception var4) {
            return DataResult.error(() -> "Failed to load player data for " + uuid);
        }

        int i = NbtUtils.getDataVersion(tag, -1);
        tag = DataFixTypes.PLAYER.updateToCurrentVersion(this.fixerUpper, tag, i);

        editor.accept(tag);
        try {
            Path path = this.playerDir.toPath();
            Path path2 = Files.createTempFile(path, uuid + "-", ".dat");
            NbtIo.writeCompressed(tag, path2);
            Path path3 = path.resolve(uuid + ".dat");
            Path path4 = path.resolve(uuid + ".dat_old");
            Util.safeReplaceFile(path3, path2, path4);
        } catch (Exception var6) {
            return DataResult.error(() -> "Failed to save player data for " + uuid);
        }
        return DataResult.success(tag);
    }
}
