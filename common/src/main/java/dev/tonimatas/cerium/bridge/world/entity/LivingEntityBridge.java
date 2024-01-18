package dev.tonimatas.cerium.bridge.world.entity;

import org.bukkit.craftbukkit.v1_20_R3.attribute.CraftAttributeMap;

import java.util.Set;
import java.util.UUID;

public interface LivingEntityBridge {
    int cerium$getExpToDrop();
    
    void cerium$setExpToDrop(int value);
    
    CraftAttributeMap cerium$getCraftAttributeMap();
    
    boolean cerium$getCollides();
    
    void cerium$setCollides(boolean value);
    
    Set<UUID> cerium$getCollidableExemptions();

    boolean cerium$getBukkitPickUpLoot();

    void cerium$setBukkitPickUpLoot(boolean value);
}
