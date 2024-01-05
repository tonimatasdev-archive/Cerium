package dev.tonimatas.cerium.forge.util.forge;

import dev.tonimatas.cerium.util.CeriumEventFactory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.SpawnData;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import org.jetbrains.annotations.Nullable;

public class CeriumEventFactoryImpl {
    public static CeriumEventFactory.FinalizeSpawn onFinalizeSpawnSpawner(Mob mob, ServerLevelAccessor level, DifficultyInstance difficulty, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag spawnTag, BaseSpawner spawner) {
        MobSpawnEvent.FinalizeSpawn  finalizeSpawn = net.minecraftforge.event.ForgeEventFactory.onFinalizeSpawnSpawner(mob, level, difficulty, spawnData, spawnTag, spawner);
        return new CeriumEventFactory.FinalizeSpawn(finalizeSpawn.getEntity(), finalizeSpawn.getLevel(), finalizeSpawn.getX(), finalizeSpawn.getX(), finalizeSpawn.getZ(), finalizeSpawn.getDifficulty(), finalizeSpawn.getSpawnType(), finalizeSpawn.getSpawnData(), finalizeSpawn.getSpawnTag(), finalizeSpawn.getSpawner());
    }

    public static boolean checkSpawnPositionSpawner(Mob mob, ServerLevelAccessor level, MobSpawnType spawnType, SpawnData spawnData, BaseSpawner spawner) {
        return net.minecraftforge.event.ForgeEventFactory.checkSpawnPositionSpawner(mob, level, spawnType, spawnData, spawner);
    }

    public static CeriumEventFactory.ChorusFruit onChorusFruitTeleport(LivingEntity entity, double targetX, double targetY, double targetZ) {
        EntityTeleportEvent.ChorusFruit chorusFruit = net.minecraftforge.event.ForgeEventFactory.onChorusFruitTeleport(entity, targetX, targetY, targetZ);
        return new CeriumEventFactory.ChorusFruit(chorusFruit.getEntityLiving(), chorusFruit.getTargetX(), chorusFruit.getTargetY(), chorusFruit.getTargetZ(), chorusFruit.isCanceled());
    }

    public static int onArrowLoose(ItemStack stack, Level level, Player player, int charge, boolean hasAmmo) {
        return ForgeEventFactory.onArrowLoose(stack, level, player, charge, hasAmmo);
    }

    public static CeriumEventFactory.TeleportCommand onEntityTeleportCommand(Entity entity, double x, double y, double z) {
        EntityTeleportEvent.TeleportCommand teleportCommand = ForgeEventFactory.onEntityTeleportCommand(entity, x, y, z);
        return new CeriumEventFactory.TeleportCommand(teleportCommand.getTargetX(), teleportCommand.getTargetY(), teleportCommand.getTargetZ(), teleportCommand.isCanceled());
    }
}
