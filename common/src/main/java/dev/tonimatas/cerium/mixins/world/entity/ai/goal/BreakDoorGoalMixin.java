package dev.tonimatas.cerium.mixins.world.entity.ai.goal;

import net.minecraft.world.entity.ai.goal.BreakDoorGoal;
import net.minecraft.world.entity.ai.goal.DoorInteractGoal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BreakDoorGoal.class)
public abstract class BreakDoorGoalMixin {
    @Shadow public abstract void start();

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;removeBlock(Lnet/minecraft/core/BlockPos;Z)Z", shift = At.Shift.BEFORE))
    private void cerium$tick(CallbackInfo ci) {
        // CraftBukkit start
        if (org.bukkit.craftbukkit.v1_20_R3.event.CraftEventFactory.callEntityBreakDoorEvent(((DoorInteractGoal) (Object) this).mob, ((DoorInteractGoal) (Object) this).doorPos).isCancelled()) {
            this.start();
            return;
        }
        // CraftBukkit end
    }
}
