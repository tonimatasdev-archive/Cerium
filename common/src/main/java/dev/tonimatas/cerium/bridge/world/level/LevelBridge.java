package dev.tonimatas.cerium.bridge.world.level;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.dimension.LevelStem;

public interface LevelBridge {
    ResourceKey<LevelStem> getTypeKey();
}
