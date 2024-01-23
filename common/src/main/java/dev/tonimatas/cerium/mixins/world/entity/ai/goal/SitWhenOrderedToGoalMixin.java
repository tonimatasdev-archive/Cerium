package dev.tonimatas.cerium.mixins.world.entity.ai.goal;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SitWhenOrderedToGoal.class)
public class SitWhenOrderedToGoalMixin {
    @Shadow @Final private TamableAnimal mob;

    /**
     * @author TonimatasDEV
     * @reason CraftBukkit
     */
    @Overwrite
    public boolean canUse() {
        if (!this.mob.isTame()) {
            return this.mob.isOrderedToSit() && this.mob.getTarget() == null; // CraftBukkit - Allow sitting for wild animals
        } else if (this.mob.isInWaterOrBubble()) {
            return false;
        } else if (!this.mob.onGround()) {
            return false;
        } else {
            LivingEntity livingEntity = this.mob.getOwner();
            if (livingEntity == null) {
                return true;
            } else {
                return this.mob.distanceToSqr(livingEntity) < 144.0 && livingEntity.getLastHurtByMob() != null ? false : this.mob.isOrderedToSit();
            }
        }
    }
}
