package dev.tonimatas.cerium.bridge.world.level;

import net.minecraft.server.level.ServerLevel;

public interface LevelAccessorBridge {
    ServerLevel bridge$getMinecraftWorld();
}
