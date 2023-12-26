package dev.tonimatas.cerium.bridge.world.level;

import net.minecraft.world.entity.Entity;

public interface LevelWriterBridge {
    boolean bridge$addFreshEntity(Entity entity, org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason reason);
}
