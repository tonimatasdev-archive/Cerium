package dev.tonimatas.cerium.mixins.world.entity.monster;

import dev.tonimatas.cerium.bridge.world.entity.EntityBridge;
import dev.tonimatas.cerium.bridge.world.entity.LivingEntityBridge;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.level.ServerLevelAccessor;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Spider.class)
public class SpiderMixin {
    @Inject(method = "finalizeSpawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/Spider;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;)Z"))
    private void cerium$finalizeSpawn(ServerLevelAccessor serverLevelAccessor, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType, SpawnGroupData spawnGroupData, CompoundTag compoundTag, CallbackInfoReturnable<SpawnGroupData> cir) {
        ((LivingEntityBridge) this).cerium$addEffectCause(EntityPotionEffectEvent.Cause.SPIDER_SPAWN);
    }
}
