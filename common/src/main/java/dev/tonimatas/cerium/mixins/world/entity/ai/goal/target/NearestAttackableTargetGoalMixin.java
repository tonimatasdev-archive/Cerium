package dev.tonimatas.cerium.mixins.world.entity.ai.goal.target;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import org.bukkit.event.entity.EntityTargetEvent;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NearestAttackableTargetGoal.class)
public class NearestAttackableTargetGoalMixin {
    @Shadow @Nullable protected LivingEntity target;

    @Inject(method = "start", at = @At(value = "HEAD"))
    private void cerium$start(CallbackInfo ci) {
        ((NearestAttackableTargetGoal<?>) (Object) this).mob.setTargetCause(target instanceof ServerPlayer ? EntityTargetEvent.TargetReason.CLOSEST_PLAYER : EntityTargetEvent.TargetReason.CLOSEST_ENTITY, true); // Cerium // CraftBukkit - Reason
    }
}
