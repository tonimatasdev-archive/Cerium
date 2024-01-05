package dev.tonimatas.cerium.mixins.server.commands;

import net.minecraft.server.commands.EffectCommands;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EffectCommands.class)
public class EffectCommandsMixin {
    @Redirect(method = "giveEffect", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z"))
    private static boolean cerium$giveEffect(LivingEntity instance, MobEffectInstance flag, Entity entity) {
        return instance.addEffect(flag, entity, org.bukkit.event.entity.EntityPotionEffectEvent.Cause.COMMAND); // CraftBukkit;
    }

    @Redirect(method = "clearEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;removeAllEffects()Z"))
    private static boolean cerium$clearEffects(LivingEntity instance) {
        return instance.removeAllEffects(org.bukkit.event.entity.EntityPotionEffectEvent.Cause.COMMAND); // CraftBukkit;
    }

    @Redirect(method = "clearEffect", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;removeEffect(Lnet/minecraft/world/effect/MobEffect;)Z"))
    private static boolean cerium$giveEffect(LivingEntity instance, MobEffect mobeffectinstance) {
        return instance.removeEffect(mobeffectinstance, org.bukkit.event.entity.EntityPotionEffectEvent.Cause.COMMAND); // CraftBukkit;
    }
}
