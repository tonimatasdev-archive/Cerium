package dev.tonimatas.cerium.mixins.world.entity.monster;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.monster.Evoker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Evoker.EvokerSummonSpellGoal.class)
public class EvokerMixin {
    @Inject(method = "performSpellCasting", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;addFreshEntityWithPassengers(Lnet/minecraft/world/entity/Entity;)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void cerium$performSpellCasting(CallbackInfo ci, ServerLevel level) {
        level.addFreshEntityWithPassengers(org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.SPELL); // CraftBukkit - Add SpawnReason
    }
}
