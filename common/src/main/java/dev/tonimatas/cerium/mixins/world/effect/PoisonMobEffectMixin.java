package dev.tonimatas.cerium.mixins.world.effect;

import dev.tonimatas.cerium.bridge.world.damagesource.DamageSourceBridge;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "net.minecraft.world.effect.PoisonMobEffect")
public class PoisonMobEffectMixin {
    @Redirect(method = "applyEffectTick", at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/world/damagesource/DamageSources;magic()Lnet/minecraft/world/damagesource/DamageSource;"))
    private DamageSource cerium$applyEffectTick(DamageSources instance) {
        return ((DamageSourceBridge) instance).bridge$poison();
    }
}
