package dev.tonimatas.cerium.bridge.world.entity;

import net.minecraft.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.phys.Vec3;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_20_R3.event.CraftPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.Optional;
import java.util.Set;

public interface EntityBridge {
    boolean bridge$getValid();
    boolean bridge$getPersist();
    void bridge$setPersist(boolean value);
    boolean bridge$getVisibleByDefault();
    void bridge$setVisibleByDefault(boolean value);
    boolean bridge$getInWorld();
    boolean bridge$getGeneration();
    void bridge$setGeneration(boolean value);
    int bridge$getMaxAirTicks();
    void bridge$setMaxAirTicks(int value);
    org.bukkit.projectiles.ProjectileSource bridge$getProjectileSource();
    void bridge$setProjectileSource(org.bukkit.projectiles.ProjectileSource value);
    void bridge$setLastDamageCancelled(boolean value);
    void bridge$setPersistentInvisibility(boolean value);
    void bridge$setPluginRemoved(boolean value);

    void bridge$setBukkitEntity(CraftEntity craftEntity);

    CraftEntity getBukkitEntity();

    int getDefaultMaxAirSupply();

    float getBukkitYaw();

    boolean isChunkLoaded();

    void postTick();

    void setSecondsOnFire(int i, boolean callEvent);

    SoundEvent getSwimSound0();

    SoundEvent getSwimSplashSound0();

    SoundEvent getSwimHighSpeedSplashSound0();

    boolean canCollideWithBukkit(Entity entity);

    boolean saveAsPassenger(CompoundTag compoundTag, boolean includeAll);

    CompoundTag saveWithoutId(CompoundTag compoundTag, boolean includeAll);

    void addAdditionalSaveData(CompoundTag compoundTag, boolean includeAll);

    boolean removePassengerBukkit(Entity entity);

    Entity teleportTo(ServerLevel serverLevel, Vec3 location);

    Optional<BlockUtil.FoundRectangle> getExitPortal(ServerLevel worldserver, BlockPos blockposition, boolean flag, WorldBorder worldborder, int searchRadius, boolean canCreatePortal, int createRadius);

    boolean teleportTo(ServerLevel worldserver, double d0, double d1, double d2, Set<RelativeMovement> set, float f, float f1, org.bukkit.event.player.PlayerTeleportEvent.TeleportCause cause);

    CraftPortalEvent callPortalEvent(Entity entity, ServerLevel exitWorldServer, Vec3 exitPosition, PlayerTeleportEvent.TeleportCause cause, int searchRadius, int creationRadius);
}
