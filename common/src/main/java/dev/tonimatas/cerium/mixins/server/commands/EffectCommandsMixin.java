package dev.tonimatas.cerium.mixins.server.commands;

import com.llamalad7.mixinextras.sugar.Local;
import dev.tonimatas.cerium.bridge.world.entity.LivingEntityBridge;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Holder;
import net.minecraft.server.commands.EffectCommands;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;

@Mixin(EffectCommands.class)
public class EffectCommandsMixin {
    @Inject(method = "giveEffect", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z"))
    private static void cerium$giveEffect(CommandSourceStack commandSourceStack, Collection<? extends Entity> collection, Holder<MobEffect> holder, Integer integer, int i, boolean bl, CallbackInfoReturnable<Integer> cir, @Local Entity entity) {
        ((LivingEntityBridge) entity).cerium$addEffectCause(org.bukkit.event.entity.EntityPotionEffectEvent.Cause.COMMAND); // CraftBukkit;
    }

    @Inject(method = "clearEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;removeAllEffects()Z"))
    private static void cerium$clearEffects(CommandSourceStack commandSourceStack, Collection<? extends Entity> collection, CallbackInfoReturnable<Integer> cir, @Local Entity entity) {
        ((LivingEntityBridge) entity).cerium$addRemoveAllEffects(org.bukkit.event.entity.EntityPotionEffectEvent.Cause.COMMAND); // CraftBukkit;
    }

    @Redirect(method = "clearEffect", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;removeEffect(Lnet/minecraft/world/effect/MobEffect;)Z"))
    private static boolean cerium$giveEffect(LivingEntity instance, MobEffect mobeffectinstance) {
        return instance.removeEffect(mobeffectinstance, org.bukkit.event.entity.EntityPotionEffectEvent.Cause.COMMAND); // CraftBukkit;
    }
}
