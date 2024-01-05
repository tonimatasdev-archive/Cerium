package dev.tonimatas.cerium.mixins.world.entity.ai.behavior;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.PrepareRamNearestTarget;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_20_R3.event.CraftEventFactory;
import org.bukkit.event.entity.EntityTargetEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PrepareRamNearestTarget.class)
public abstract class PrepareRamNearestTargetMixin {
    @Shadow @Final private TargetingConditions ramTargeting;

    @Shadow protected abstract void chooseRamPosition(PathfinderMob arg, LivingEntity arg2);

    /**
     * @author TonimatasDEV
     * @reason CraftBukkit
     */
    @Overwrite
    protected void start(ServerLevel serverLevel, PathfinderMob pathfinderMob, long l) {
        Brain<?> brain = pathfinderMob.getBrain();
        brain.getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES).flatMap((nearestVisibleLivingEntities) -> {
            return nearestVisibleLivingEntities.findClosest((livingEntity) -> {
                return this.ramTargeting.test(pathfinderMob, livingEntity);
            });
        }).ifPresent((livingEntity) -> {
            // CraftBukkit start
            EntityTargetEvent event = CraftEventFactory.callEntityTargetLivingEvent(pathfinderMob, livingEntity, (livingEntity instanceof ServerPlayer) ? EntityTargetEvent.TargetReason.CLOSEST_PLAYER : EntityTargetEvent.TargetReason.CLOSEST_ENTITY);
            if (event.isCancelled() || event.getTarget() == null) {
                return;
            }
            livingEntity = ((CraftLivingEntity) event.getTarget()).getHandle();
            // CraftBukkit end
            this.chooseRamPosition(pathfinderMob, livingEntity);
        });
    }
}
