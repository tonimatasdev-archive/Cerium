package dev.tonimatas.cerium.bridge.world.level.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;

import java.util.List;

public interface PersistentEntitySectionManagerBridge {
    List<Entity> getEntities(ChunkPos chunkCoordIntPair);
    boolean isPending(long pair);
}
