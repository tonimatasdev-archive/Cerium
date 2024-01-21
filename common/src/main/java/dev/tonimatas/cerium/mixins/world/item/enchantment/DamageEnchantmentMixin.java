package dev.tonimatas.cerium.mixins.world.item.enchantment;

import dev.tonimatas.cerium.bridge.world.entity.LivingEntityBridge;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.DamageEnchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DamageEnchantment.class)
public class DamageEnchantmentMixin {
    @Inject(method = "doPostAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;)Z"))
    private void cerium$doPostAttack(LivingEntity livingEntity, Entity entity, int i, CallbackInfo ci) {
        ((LivingEntityBridge) livingEntity).cerium$addEffectCause(org.bukkit.event.entity.EntityPotionEffectEvent.Cause.ATTACK);
    }
}
