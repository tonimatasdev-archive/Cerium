package dev.tonimatas.cerium.util;

import joptsimple.OptionSet;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityExhaustionEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.spongepowered.asm.mixin.Unique;

import java.util.concurrent.atomic.AtomicReference;

public class CeriumValues {
    public static BlockPos openSign; // CraftBukkit
    public static AtomicReference<EntityPotionEffectEvent.Cause> potionEffectCause = new AtomicReference<>(EntityPotionEffectEvent.Cause.UNKNOWN); // CraftBukkit
    public static AtomicReference<EntityExhaustionEvent.ExhaustionReason> exhaustionReason = new AtomicReference<>(EntityExhaustionEvent.ExhaustionReason.UNKNOWN); // CraftBukkit
    public static AtomicReference<OptionSet> optionSet = new AtomicReference<>(null);
    public static final LootContextParam<Integer> LOOTING_MOD = new LootContextParam<>(new ResourceLocation("bukkit:looting_mod")); // CraftBukkit
    public static CeriumClasses.WorldInfo worldInfo = null;
    public static ResourceKey<LevelStem> dimensionType = null;
    public static InteractionHand hand = null;
    public static AtomicReference<CreatureSpawnEvent.SpawnReason> spawnReason = new AtomicReference<>(CreatureSpawnEvent.SpawnReason.DEFAULT);
    @Unique
    public static AtomicReference<CreatureSpawnEvent.SpawnReason> spawnReason$trySpawnMob = new AtomicReference<>(CreatureSpawnEvent.SpawnReason.DEFAULT);
}
