package dev.tonimatas.cerium.bridge.world.entity;

import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;

public interface EntityBridge {
    boolean bridge$getValid();
    boolean bridge$getPersist();
    void bridge$setPersist(boolean value);
    boolean bridge$getVisibleByDefault();
    void bridge$setVisibleByDefault(boolean value);
    boolean bridge$getInWorld();
    int bridge$getMaxAirTicks();
    void bridge$setMaxAirTicks(int value);
    org.bukkit.projectiles.ProjectileSource bridge$getProjectileSource();
    void bridge$setProjectileSource(org.bukkit.projectiles.ProjectileSource value);
    void bridge$setLastDamageCancelled(boolean value);
    void bridge$setPersistentInvisibility(boolean value);
    void bridge$setPluginRemoved(boolean value);

    CraftEntity getBukkitEntity();

    int getDefaultMaxAirSupply();

    float getBukkitYaw();

    boolean isChunkLoaded();

    void postTick();


}
