package dev.tonimatas.cerium.mixins.world.entity.ai.goal.target;

import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import org.bukkit.event.entity.EntityTargetEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OwnerHurtByTargetGoal.class)
public class OwnerHurtByTargetGoalMixin {
    @Inject(method = "start", at = @At(value = "HEAD"))
    private void cerium$start(CallbackInfo ci) {
        ((OwnerHurtByTargetGoal) (Object) this).mob.setTargetCause(EntityTargetEvent.TargetReason.TARGET_ATTACKED_OWNER, true); // Cerium // CraftBukkit - Reason
    }
}
