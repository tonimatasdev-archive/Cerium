package dev.tonimatas.cerium.mixins.world.effect;

import net.minecraft.world.entity.LivingEntity;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.world.effect.RegenerationMobEffect")
public class RegenerationMobEffectMixin {
    @Inject(method = "applyEffectTick", at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/world/entity/LivingEntity;heal(F)V"))
    private void cerium$applyEffectTick(LivingEntity livingEntity, int amplifier, CallbackInfo ci) {
        ((LivingEntityBridge) livingEntity).bridge$addHealReason(EntityRegainHealthEvent.RegainReason.MAGIC_REGEN);
    }
}
