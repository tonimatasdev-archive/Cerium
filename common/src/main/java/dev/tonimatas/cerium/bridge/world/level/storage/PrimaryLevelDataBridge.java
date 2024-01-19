package dev.tonimatas.cerium.bridge.world.level.storage;

import net.minecraft.core.Registry;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.dimension.LevelStem;

public interface PrimaryLevelDataBridge {
    Registry<LevelStem> cerium$getCustomDimension();
    
    void cerium$setCustomDimension(Registry<LevelStem> value);

    Tag cerium$getPDC();
    
    void cerium$setPDC(Tag value);
    
    void checkName(String name);
}
