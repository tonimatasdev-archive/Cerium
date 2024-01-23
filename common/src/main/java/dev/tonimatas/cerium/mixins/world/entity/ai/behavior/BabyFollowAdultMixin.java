package dev.tonimatas.cerium.mixins.world.entity.ai.behavior;

import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BabyFollowAdult;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.behavior.OneShot;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_20_R3.event.CraftEventFactory;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.Inject;

import java.util.function.Function;

@Mixin(BabyFollowAdult.class)
public class BabyFollowAdultMixin {
     /**
      * @author TonimatasDEV
      * @reason CraftBukkit
      */
     @Overwrite
     public static OneShot<AgeableMob> create(UniformInt uniformInt, Function<LivingEntity, Float> function) {
         return BehaviorBuilder.create((instance) -> {
             return instance.group(instance.present(MemoryModuleType.NEAREST_VISIBLE_ADULT), instance.registered(MemoryModuleType.LOOK_TARGET), instance.absent(MemoryModuleType.WALK_TARGET)).apply(instance, (memoryAccessor, memoryAccessor2, memoryAccessor3) -> {
                 return (serverLevel, ageableMob, l) -> {
                     if (!ageableMob.isBaby()) {
                         return false;
                     } else {
                         LivingEntity ageableMob2 = (AgeableMob)instance.get(memoryAccessor);
                         if (ageableMob.closerThan(ageableMob2, (double)(uniformInt.getMaxValue() + 1)) && !ageableMob.closerThan(ageableMob2, (double)uniformInt.getMinValue())) {
                             // CraftBukkit start
                             EntityTargetLivingEntityEvent event = CraftEventFactory.callEntityTargetLivingEvent(ageableMob, ageableMob2, EntityTargetEvent.TargetReason.FOLLOW_LEADER);
                             if (event.isCancelled()) {
                                 return false;
                             }
                             if (event.getTarget() == null) {
                                 memoryAccessor.erase();
                                 return true;
                             }
                             ageableMob2 = ((CraftLivingEntity) event.getTarget()).getHandle();
                             // CraftBukkit end
                             WalkTarget walkTarget = new WalkTarget(new EntityTracker(ageableMob2, false), (Float)function.apply(ageableMob), uniformInt.getMinValue() - 1);
                             memoryAccessor2.set(new EntityTracker(ageableMob2, true));
                             memoryAccessor3.set(walkTarget);
                             return true;
                         } else {
                             return false;
                         }
                     }
                 };
             });
         });
     }
}
