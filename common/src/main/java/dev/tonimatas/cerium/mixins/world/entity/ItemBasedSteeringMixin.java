package dev.tonimatas.cerium.mixins.world.entity;

import dev.tonimatas.cerium.bridge.world.entity.ItemBasedSteeringBridge;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.ItemBasedSteering;
import org.spongepowered.asm.mixin.*;

@Mixin(ItemBasedSteering.class)
public class ItemBasedSteeringMixin implements ItemBasedSteeringBridge {

    @Shadow public boolean boosting;

    @Shadow public int boostTime;

    @Shadow @Final private SynchedEntityData entityData;

    @Shadow @Final private EntityDataAccessor<Integer> boostTimeAccessor;

    // CraftBukkit add setBoostTicks(int)
    @Override
    public void setBoostTicks(int ticks) {
        this.boosting = true;
        this.boostTime = 0;
        this.entityData.set(this.boostTimeAccessor, ticks);
    }
    // CraftBukkit end
}
