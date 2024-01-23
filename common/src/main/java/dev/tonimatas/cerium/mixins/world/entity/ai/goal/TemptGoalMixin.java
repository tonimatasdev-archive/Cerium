package dev.tonimatas.cerium.mixins.world.entity.ai.goal;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.SwellGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_20_R3.event.CraftEventFactory;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

@Mixin(TemptGoal.class)
public class TemptGoalMixin {
    @Shadow private int calmDown;

    @Shadow @Nullable protected Player player;

    @Shadow @Final protected PathfinderMob mob;
    @Shadow @Final private TargetingConditions targetingConditions;

    @Unique protected LivingEntity cerium$livingEntity; // Cerium
    
    /**
     * @author TonimatasDEV
     * @reason CraftBukkit
     */
    @Overwrite
    public boolean canUse() {
        if (this.calmDown > 0) {
            --this.calmDown;
            return false;
        } else {
            this.player = this.mob.level().getNearestPlayer(this.targetingConditions, this.mob);
            // CraftBukkit start
            if (this.player != null) {
                EntityTargetLivingEntityEvent event = CraftEventFactory.callEntityTargetLivingEvent(this.mob, this.player, EntityTargetEvent.TargetReason.TEMPT);
                if (event.isCancelled()) {
                    return false;
                }
                // CraftBukkit end
                // Cerium start
                LivingEntity livingEntity = (event.getTarget() == null) ? null : ((CraftLivingEntity) event.getTarget()).getHandle();

                if (livingEntity == null) {
                    this.player = null;
                } else if (livingEntity instanceof Player player) {
                    this.player = player;
                } else {
                    this.cerium$livingEntity = livingEntity;
                }
                // Cerium end
            }
            return this.player != null;
        }
    }
    
    // TODO: Delete player and use cerium$livingEntity
}
