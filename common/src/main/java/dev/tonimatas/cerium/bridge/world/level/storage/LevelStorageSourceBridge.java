package dev.tonimatas.cerium.bridge.world.level.storage;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.validation.ContentValidationException;

import java.io.IOException;

public interface LevelStorageSourceBridge {
    LevelStorageSource.LevelStorageAccess validateAndCreateAccess(String s, ResourceKey<LevelStem> dimensionType) throws IOException, ContentValidationException;

    LevelStorageSource.LevelStorageAccess createAccess(String s, ResourceKey<LevelStem> dimensionType) throws IOException;
}
