package dev.tonimatas.cerium.neoforge.util.neoforge;

import dev.tonimatas.cerium.util.CeriumEventFactory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.SpawnData;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent;
import net.neoforged.neoforge.event.entity.living.MobSpawnEvent;
import org.jetbrains.annotations.Nullable;

public class CeriumEventFactoryImpl {
    public static CeriumEventFactory.FinalizeSpawn onFinalizeSpawnSpawner(Mob mob, ServerLevelAccessor level, DifficultyInstance difficulty, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag spawnTag, BaseSpawner spawner) {
        MobSpawnEvent.FinalizeSpawn  finalizeSpawn = EventHooks.onFinalizeSpawnSpawner(mob, level, difficulty, spawnData, spawnTag, spawner);
        return new CeriumEventFactory.FinalizeSpawn(finalizeSpawn.getEntity(), finalizeSpawn.getLevel(), finalizeSpawn.getX(), finalizeSpawn.getX(), finalizeSpawn.getZ(), finalizeSpawn.getDifficulty(), finalizeSpawn.getSpawnType(), finalizeSpawn.getSpawnData(), finalizeSpawn.getSpawnTag(), finalizeSpawn.getSpawner());
    }

    public static boolean checkSpawnPositionSpawner(Mob mob, ServerLevelAccessor level, MobSpawnType spawnType, SpawnData spawnData, BaseSpawner spawner) {
        return EventHooks.checkSpawnPositionSpawner(mob, level, spawnType, spawnData, spawner);
    }

    public static CeriumEventFactory.ChorusFruit onChorusFruitTeleport(LivingEntity entity, double targetX, double targetY, double targetZ) {
        EntityTeleportEvent.ChorusFruit chorusFruit = EventHooks.onChorusFruitTeleport(entity, targetX, targetY, targetZ);
        return new CeriumEventFactory.ChorusFruit(chorusFruit.getEntityLiving(), chorusFruit.getTargetX(), chorusFruit.getTargetY(), chorusFruit.getTargetZ(), chorusFruit.isCanceled());
    }

    public static int onArrowLoose(ItemStack stack, Level level, Player player, int charge, boolean hasAmmo) {
        return EventHooks.onArrowLoose(stack, level, player, charge, hasAmmo);
    }
}
