package dev.tonimatas.cerium.mixins.world.entity.ai.goal.target;

import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import org.bukkit.event.entity.EntityTargetEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HurtByTargetGoal.class)
public class HurtByTargetGoalMixin {
    @Inject(method = "start", at = @At(value = "HEAD"))
    private void cerium$start(CallbackInfo ci) {
        ((HurtByTargetGoal) (Object) this).mob.setTargetCause(EntityTargetEvent.TargetReason.TARGET_ATTACKED_ENTITY, true); // Cerium // CraftBukkit - Reason
    }

    @Inject(method = "alertOther", at = @At(value = "HEAD"))
    private void cerium$alertOther(CallbackInfo ci) {
        ((HurtByTargetGoal) (Object) this).mob.setTargetCause(EntityTargetEvent.TargetReason.TARGET_ATTACKED_NEARBY_ENTITY, true); // Cerium // CraftBukkit - Reason
    }
}
