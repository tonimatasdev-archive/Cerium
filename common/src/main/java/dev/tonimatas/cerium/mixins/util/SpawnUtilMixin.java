package dev.tonimatas.cerium.mixins.util;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.SpawnUtil;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Mixin(SpawnUtil.class)
public abstract class SpawnUtilMixin {
    @Unique private static AtomicReference<CreatureSpawnEvent.SpawnReason> cerium$spawnReason = new AtomicReference<>(CreatureSpawnEvent.SpawnReason.DEFAULT);

    @Unique
    private static <T extends Mob> Optional<T> trySpawnMob(EntityType<T> entitytypes, MobSpawnType enummobspawn, ServerLevel worldserver, BlockPos blockposition, int i, int j, int k, SpawnUtil.Strategy spawnutil_a, CreatureSpawnEvent.SpawnReason reason) {
        cerium$spawnReason.set(reason);
        return SpawnUtil.trySpawnMob(entitytypes, enummobspawn, worldserver, blockposition, i, j, k, spawnutil_a);
    }

    @Inject(method = "trySpawnMob", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;addFreshEntityWithPassengers(Lnet/minecraft/world/entity/Entity;)V", shift = At.Shift.BEFORE))
    private static <T extends Mob> void cerium$trySpawnMob(EntityType<T> entityType, MobSpawnType mobSpawnType, ServerLevel serverLevel, BlockPos blockPos, int i, int j, int k, SpawnUtil.Strategy strategy, CallbackInfoReturnable<Optional<T>> cir) {
        serverLevel.addFreshEntityWithPassengersReason(cerium$spawnReason.getAndSet(CreatureSpawnEvent.SpawnReason.DEFAULT));
    }
}
