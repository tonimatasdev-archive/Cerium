package dev.tonimatas.cerium.neoforge.util.neoforge;

import dev.tonimatas.cerium.util.ForgeEventFactory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.SpawnData;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.entity.living.MobSpawnEvent;
import org.jetbrains.annotations.Nullable;

public class ForgeEventFactoryImpl {
    public static ForgeEventFactory.FinalizeSpawn onFinalizeSpawnSpawner(Mob mob, ServerLevelAccessor level, DifficultyInstance difficulty, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag spawnTag, BaseSpawner spawner) {
        MobSpawnEvent.FinalizeSpawn  finalizeSpawn = EventHooks.onFinalizeSpawnSpawner(mob, level, difficulty, spawnData, spawnTag, spawner);
        return new ForgeEventFactory.FinalizeSpawn(finalizeSpawn.getEntity(), finalizeSpawn.getLevel(), finalizeSpawn.getX(), finalizeSpawn.getX(), finalizeSpawn.getZ(), finalizeSpawn.getDifficulty(), finalizeSpawn.getSpawnType(), finalizeSpawn.getSpawnData(), finalizeSpawn.getSpawnTag(), finalizeSpawn.getSpawner());
    }

    public static boolean checkSpawnPositionSpawner(Mob mob, ServerLevelAccessor level, MobSpawnType spawnType, SpawnData spawnData, BaseSpawner spawner) {
        return EventHooks.checkSpawnPositionSpawner(mob, level, spawnType, spawnData, spawner);
    }
}
