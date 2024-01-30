package dev.tonimatas.cerium.mixins.world.entity.monster.warden;

import dev.tonimatas.cerium.util.CeriumValues;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.phys.Vec3;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Warden.class)
public class WardenMixin {
    @Inject(method = "applyDarknessAround", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/effect/MobEffectUtil;addEffectToPlayersAround(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/Vec3;DLnet/minecraft/world/effect/MobEffectInstance;I)Ljava/util/List;"))
    private static void cerium$applyDarknessAround(ServerLevel serverLevel, Vec3 vec3, Entity entity, int i, CallbackInfo ci) {
        CeriumValues.potionEffectCause.set(EntityPotionEffectEvent.Cause.WARDEN); // Cerium // CraftBukkit
    }
}
