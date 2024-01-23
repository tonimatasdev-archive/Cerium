package dev.tonimatas.cerium.mixins.world.entity.ai.goal.target;

import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import org.bukkit.event.entity.EntityTargetEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OwnerHurtTargetGoal.class)
public class OwnerHurtTargetGoalMixin {
    @Inject(method = "start", at = @At(value = "HEAD"))
    private void cerium$start(CallbackInfo ci) {
        ((OwnerHurtTargetGoal) (Object) this).mob.setTargetCause(EntityTargetEvent.TargetReason.OWNER_ATTACKED_TARGET, true); // Cerium // CraftBukkit - Reason
    }
}
