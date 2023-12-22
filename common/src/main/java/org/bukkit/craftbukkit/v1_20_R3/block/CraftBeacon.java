package org.bukkit.craftbukkit.v1_20_R3.block;

import java.util.ArrayList;
import java.util.Collection;
import net.minecraft.world.ChestLock;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityBeacon;
import org.bukkit.World;
import org.bukkit.block.Beacon;
import org.bukkit.craftbukkit.v1_20_R3.potion.CraftPotionEffectType;
import org.bukkit.craftbukkit.v1_20_R3.util.CraftChatMessage;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class CraftBeacon extends CraftBlockEntityState<BlockEntityBeacon> implements Beacon {

    public CraftBeacon(World world, BlockEntityBeacon tileEntity) {
        super(world, tileEntity);
    }

    protected CraftBeacon(CraftBeacon state) {
        super(state);
    }

    @Override
    public Collection<LivingEntity> getEntitiesInRange() {
        ensureNoWorldGeneration();

        BlockEntity tileEntity = this.getBlockEntityFromWorld();
        if (tileEntity instanceof BlockEntityBeacon) {
            BlockEntityBeacon beacon = (BlockEntityBeacon) tileEntity;

            Collection<EntityHuman> nms = BlockEntityBeacon.getHumansInRange(beacon.getLevel(), beacon.getBlockPos(), beacon.levels);
            Collection<LivingEntity> bukkit = new ArrayList<LivingEntity>(nms.size());

            for (EntityHuman human : nms) {
                bukkit.add(human.getBukkitEntity());
            }

            return bukkit;
        }

        // block is no longer a beacon
        return new ArrayList<LivingEntity>();
    }

    @Override
    public int getTier() {
        return this.getSnapshot().levels;
    }

    @Override
    public PotionEffect getPrimaryEffect() {
        return this.getSnapshot().getPrimaryEffect();
    }

    @Override
    public void setPrimaryEffect(PotionEffectType effect) {
        this.getSnapshot().primaryPower = (effect != null) ? CraftPotionEffectType.bukkitToMinecraft(effect) : null;
    }

    @Override
    public PotionEffect getSecondaryEffect() {
        return this.getSnapshot().getSecondaryEffect();
    }

    @Override
    public void setSecondaryEffect(PotionEffectType effect) {
        this.getSnapshot().secondaryPower = (effect != null) ? CraftPotionEffectType.bukkitToMinecraft(effect) : null;
    }

    @Override
    public String getCustomName() {
        BlockEntityBeacon beacon = this.getSnapshot();
        return beacon.name != null ? CraftChatMessage.fromComponent(beacon.name) : null;
    }

    @Override
    public void setCustomName(String name) {
        this.getSnapshot().setCustomName(CraftChatMessage.fromStringOrNull(name));
    }

    @Override
    public boolean isLocked() {
        return !this.getSnapshot().lockKey.key.isEmpty();
    }

    @Override
    public String getLock() {
        return this.getSnapshot().lockKey.key;
    }

    @Override
    public void setLock(String key) {
        this.getSnapshot().lockKey = (key == null) ? ChestLock.NO_LOCK : new ChestLock(key);
    }

    @Override
    public CraftBeacon copy() {
        return new CraftBeacon(this);
    }
}
