package dev.tonimatas.cerium.bridge.world.level;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.dimension.LevelStem;
import org.bukkit.craftbukkit.v1_20_R3.CraftServer;
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld;

public interface LevelBridge {
    ResourceKey<LevelStem> getTypeKey();

    CraftServer getCraftServer();

    CraftWorld getWorld();
}
