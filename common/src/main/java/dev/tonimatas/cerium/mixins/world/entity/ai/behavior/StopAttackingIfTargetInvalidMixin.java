package dev.tonimatas.cerium.mixins.world.entity.ai.behavior;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.StopAttackingIfTargetInvalid;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_20_R3.event.CraftEventFactory;
import org.bukkit.event.entity.EntityTargetEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.function.BiConsumer;
import java.util.function.Predicate;

@Mixin(StopAttackingIfTargetInvalid.class)
public abstract class StopAttackingIfTargetInvalidMixin {
    /**
     * @author TonimatasDEV
     * @reason CraftBukkit
     */
    @Overwrite
    public static <E extends Mob> BehaviorControl<E> create(Predicate<LivingEntity> predicate, BiConsumer<E, LivingEntity> biConsumer, boolean bl) {
        return BehaviorBuilder.create((instance) -> {
            return instance.group(instance.present(MemoryModuleType.ATTACK_TARGET), instance.registered(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE)).apply(instance, (memoryAccessor, memoryAccessor2) -> {
                return (serverLevel, mob, l) -> {
                    LivingEntity livingEntity = (LivingEntity)instance.get(memoryAccessor);
                    if (mob.canAttack(livingEntity) && (!bl || !StopAttackingIfTargetInvalid.isTiredOfTryingToReachTarget(mob, instance.tryGet(memoryAccessor2))) && livingEntity.isAlive() && livingEntity.level() == mob.level() && !predicate.test(livingEntity)) {
                        return true;
                    } else {
                        // CraftBukkit start
                        LivingEntity old = mob.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).orElse(null);
                        EntityTargetEvent event = CraftEventFactory.callEntityTargetLivingEvent(mob, null, (old != null && !old.isAlive()) ? EntityTargetEvent.TargetReason.TARGET_DIED : EntityTargetEvent.TargetReason.FORGOT_TARGET);
                        if (event.isCancelled()) {
                            return false;
                        }
                        if (event.getTarget() == null) {
                            memoryAccessor.erase();
                            return true;
                        }
                        livingEntity = ((CraftLivingEntity) event.getTarget()).getHandle();
                        // CraftBukkit end
                        biConsumer.accept(mob, livingEntity);
                        memoryAccessor.erase();
                        return true;
                    }
                };
            });
        });
    }
}
