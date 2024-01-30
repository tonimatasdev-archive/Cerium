package dev.tonimatas.cerium.bridge.world.entity;

import net.minecraft.world.entity.LivingEntity;
import org.bukkit.craftbukkit.v1_20_R3.attribute.CraftAttributeMap;
import org.bukkit.event.entity.EntityPotionEffectEvent;

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
    
    void cerium$addRemoveAllEffects(EntityPotionEffectEvent.Cause cause);

    void cerium$addEffectCause(EntityPotionEffectEvent.Cause cause);
    
    void cerium$addRemoveEffectCause(EntityPotionEffectEvent.Cause cause);
}
