package dev.tonimatas.cerium.mixins.world.entity.monster;

import dev.tonimatas.cerium.bridge.world.entity.LivingEntityBridge;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.WitherSkeleton;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WitherSkeleton.class)
public class WitherSkeletonMixin {
    @Inject(method = "doHurtTarget", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z"))
    private void cerium$doHurtTarget(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        ((LivingEntityBridge) entity).cerium$addEffectCause(EntityPotionEffectEvent.Cause.ATTACK);
    }
}