package org.bukkit.craftbukkit.v1_20_R3.entity;

import com.google.common.base.Preconditions;
import dev.tonimatas.cerium.bridge.world.entity.EntityBridge;
import org.bukkit.craftbukkit.v1_20_R3.CraftServer;
import org.bukkit.entity.Fireball;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

public class CraftFireball extends AbstractProjectile implements Fireball {
    public CraftFireball(CraftServer server, net.minecraft.world.entity.projectile.AbstractHurtingProjectile entity) {
        super(server, entity);
    }

    @Override
    public float getYield() {
        return getHandle().bukkitYield;
    }

    @Override
    public boolean isIncendiary() {
        return getHandle().isIncendiary;
    }

    @Override
    public void setIsIncendiary(boolean isIncendiary) {
        getHandle().isIncendiary = isIncendiary;
    }

    @Override
    public void setYield(float yield) {
        getHandle().bukkitYield = yield;
    }

    @Override
    public ProjectileSource getShooter() {
        return ((EntityBridge) getHandle()).bridge$getProjectileSource();
    }

    @Override
    public void setShooter(ProjectileSource shooter) {
        if (shooter instanceof CraftLivingEntity) {
            getHandle().setOwner(((CraftLivingEntity) shooter).getHandle());
        } else {
            getHandle().setOwner(null);
        }
        ((EntityBridge) getHandle()).bridge$setProjectileSource(shooter);
    }

    @Override
    public Vector getDirection() {
        return new Vector(getHandle().xPower, getHandle().yPower, getHandle().zPower);
    }

    @Override
    public void setDirection(Vector direction) {
        Preconditions.checkArgument(direction != null, "Vector direction cannot be null");
        getHandle().setDirection(direction.getX(), direction.getY(), direction.getZ());
        update(); // SPIGOT-6579
    }

    @Override
    public net.minecraft.world.entity.projectile.AbstractHurtingProjectile getHandle() {
        return (net.minecraft.world.entity.projectile.AbstractHurtingProjectile) entity;
    }

    @Override
    public String toString() {
        return "CraftFireball";
    }
}
