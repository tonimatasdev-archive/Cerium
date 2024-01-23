package dev.tonimatas.cerium.mixins.world.entity.ai.goal.target;

import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import org.bukkit.event.entity.EntityTargetEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TargetGoal.class)
public class TargetGoalMixin {
    @Inject(method = "canContinueToUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;setTarget(Lnet/minecraft/world/entity/LivingEntity;)V"))
    private void cerium$start(CallbackInfoReturnable<Boolean> cir) {
        ((TargetGoal) (Object) this).mob.setTargetCause(EntityTargetEvent.TargetReason.CLOSEST_ENTITY, true); // Cerium // CraftBukkit - Reason
    }
    
    @Inject(method = "stop", at = @At(value = "HEAD"))
    private void cerium$start(CallbackInfo ci) {
        ((TargetGoal) (Object) this).mob.setTargetCause(EntityTargetEvent.TargetReason.FORGOT_TARGET, true); // Cerium // CraftBukkit - Reason
    }
}
