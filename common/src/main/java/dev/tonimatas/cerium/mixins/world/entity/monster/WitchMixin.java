package dev.tonimatas.cerium.mixins.world.entity.monster;

import dev.tonimatas.cerium.bridge.world.entity.LivingEntityBridge;
import net.minecraft.world.entity.monster.Witch;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Witch.class)
public class WitchMixin {
    @Inject(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/Witch;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;)Z"))
    private void cerium$aiStep(CallbackInfo ci) {
        ((LivingEntityBridge) this).cerium$addEffectCause(EntityPotionEffectEvent.Cause.ATTACK);
    }
}
