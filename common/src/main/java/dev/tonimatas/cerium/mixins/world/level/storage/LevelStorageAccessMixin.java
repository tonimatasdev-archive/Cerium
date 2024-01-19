package dev.tonimatas.cerium.mixins.world.level.storage;

import dev.tonimatas.cerium.util.CeriumValues;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.nio.file.Path;

@Mixin(LevelStorageSource.LevelStorageAccess.class)
public class LevelStorageAccessMixin {
    @Shadow @Final public LevelStorageSource.LevelDirectory levelDirectory;
    @Unique public ResourceKey<LevelStem> dimensionType;
    
    @Inject(method = "<init>", at = @At(value = "TAIL"))
    private void cerium$init(LevelStorageSource levelStorageSource, String string, Path path, CallbackInfo ci) {
        this.dimensionType = CeriumValues.dimensionType;
    }

    // CraftBukkit start
    @Unique
    private static Path getStorageFolder(Path path, ResourceKey<LevelStem> dimensionType) {
        if (dimensionType == LevelStem.OVERWORLD) {
            return path;
        } else if (dimensionType == LevelStem.NETHER) {
            return path.resolve("DIM-1");
        } else if (dimensionType == LevelStem.END) {
            return path.resolve("DIM1");
        } else {
            return path.resolve("dimensions").resolve(dimensionType.location().getNamespace()).resolve(dimensionType.location().getPath());
        }
    }
    // CraftBukkit end
    
    @Inject(method = "getDimensionPath", at = @At(value = "RETURN"), cancellable = true)
    private void cerium$getDimensionPath(ResourceKey<Level> resourceKey, CallbackInfoReturnable<Path> cir) {
        cir.setReturnValue(getStorageFolder(this.levelDirectory.path(), this.dimensionType));
    }
}
