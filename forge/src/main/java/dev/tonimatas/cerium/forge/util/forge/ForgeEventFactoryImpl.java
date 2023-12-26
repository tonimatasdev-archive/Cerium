package dev.tonimatas.cerium.forge.util.forge;

import dev.tonimatas.cerium.util.ForgeEventFactory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.SpawnData;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import org.jetbrains.annotations.Nullable;

public class ForgeEventFactoryImpl {
    public static ForgeEventFactory.FinalizeSpawn onFinalizeSpawnSpawner(Mob mob, ServerLevelAccessor level, DifficultyInstance difficulty, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag spawnTag, BaseSpawner spawner) {
        MobSpawnEvent.FinalizeSpawn  finalizeSpawn = net.minecraftforge.event.ForgeEventFactory.onFinalizeSpawnSpawner(mob, level, difficulty, spawnData, spawnTag, spawner);
        return new ForgeEventFactory.FinalizeSpawn(finalizeSpawn.getEntity(), finalizeSpawn.getLevel(), finalizeSpawn.getX(), finalizeSpawn.getX(), finalizeSpawn.getZ(), finalizeSpawn.getDifficulty(), finalizeSpawn.getSpawnType(), finalizeSpawn.getSpawnData(), finalizeSpawn.getSpawnTag(), finalizeSpawn.getSpawner());
    }

    public static boolean checkSpawnPositionSpawner(Mob mob, ServerLevelAccessor level, MobSpawnType spawnType, SpawnData spawnData, BaseSpawner spawner) {
        return net.minecraftforge.event.ForgeEventFactory.checkSpawnPositionSpawner(mob, level, spawnType, spawnData, spawner);
    }
}
