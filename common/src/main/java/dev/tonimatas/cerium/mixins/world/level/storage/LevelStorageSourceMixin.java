package dev.tonimatas.cerium.mixins.world.level.storage;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.Lifecycle;
import dev.tonimatas.cerium.bridge.world.level.storage.LevelStorageSourceBridge;
import dev.tonimatas.cerium.bridge.world.level.storage.PrimaryLevelDataBridge;
import dev.tonimatas.cerium.util.CeriumValues;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.WorldDimensions;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.storage.LevelDataAndDimensions;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.PrimaryLevelData;
import net.minecraft.world.level.validation.ContentValidationException;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.io.IOException;
import java.util.Objects;

@Mixin(LevelStorageSource.class)
public abstract class LevelStorageSourceMixin implements LevelStorageSourceBridge {
    @Shadow public abstract LevelStorageSource.LevelStorageAccess createAccess(String string) throws IOException;

    @Shadow public abstract LevelStorageSource.LevelStorageAccess validateAndCreateAccess(String string) throws IOException, ContentValidationException;

    @Shadow @Final private static Logger LOGGER;

    /**
     * @author TonimatasDEV
     * @reason CraftBukkit
     */
    @Overwrite
    public static LevelDataAndDimensions getLevelDataAndDimensions(Dynamic<?> dynamic, WorldDataConfiguration worldDataConfiguration, Registry<LevelStem> registry, RegistryAccess.Frozen frozen) {
        Dynamic<?> dynamic2 = LevelStorageSource.wrapWithRegistryOps(dynamic, frozen);
        Dynamic<?> dynamic3 = dynamic2.get("WorldGenSettings").orElseEmptyMap();
        DataResult var10000 = WorldGenSettings.CODEC.parse(dynamic3);
        Logger var10003 = LOGGER;
        Objects.requireNonNull(var10003);
        WorldGenSettings worldGenSettings = (WorldGenSettings)var10000.getOrThrow(false, Util.prefix("WorldGenSettings: ", var10003::error));
        LevelSettings levelSettings = LevelSettings.parse(dynamic2, worldDataConfiguration);
        WorldDimensions.Complete complete = worldGenSettings.dimensions().bake(registry);
        Lifecycle lifecycle = complete.lifecycle().add(frozen.allRegistriesLifecycle());
        PrimaryLevelData primaryLevelData = PrimaryLevelData.parse(dynamic2, levelSettings, complete.specialWorldProperty(), worldGenSettings.options(), lifecycle);
        ((PrimaryLevelDataBridge) primaryLevelData).cerium$setPDC(((Dynamic<Tag>) dynamic2).getElement("BukkitValues", null)); // CraftBukkit - Add PDC to world
        return new LevelDataAndDimensions(primaryLevelData, complete);
    }
    
    @Override
    public LevelStorageSource.LevelStorageAccess validateAndCreateAccess(String s, ResourceKey<LevelStem> dimensionType) throws IOException, ContentValidationException { // CraftBukkit
        CeriumValues.dimensionType = dimensionType;
        return validateAndCreateAccess(s);
    }
    
    @Override
    public LevelStorageSource.LevelStorageAccess createAccess(String s, ResourceKey<LevelStem> dimensionType) throws IOException { // CraftBukkit
        CeriumValues.dimensionType = dimensionType;
        return createAccess(s); // CraftBukkit
    }
}
