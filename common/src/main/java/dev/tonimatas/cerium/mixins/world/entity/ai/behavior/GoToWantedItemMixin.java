package dev.tonimatas.cerium.mixins.world.entity.ai.behavior;

import com.mojang.datafixers.kinds.K1;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.behavior.GoToWantedItem;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.behavior.declarative.MemoryAccessor;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.entity.item.ItemEntity;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_20_R3.event.CraftEventFactory;
import org.bukkit.event.entity.EntityTargetEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.function.Predicate;

@Mixin(GoToWantedItem.class)
public class GoToWantedItemMixin {
    /**
     * @author TonimatasDEV
     * @reason CraftBukkit
     */
    @Overwrite
    public static <E extends LivingEntity> BehaviorControl<E> create(Predicate<E> predicate, float f, boolean bl, int i) {
        return BehaviorBuilder.create((instance) -> {
            BehaviorBuilder<E, ? extends MemoryAccessor<? extends K1, WalkTarget>> behaviorBuilder = bl ? instance.registered(MemoryModuleType.WALK_TARGET) : instance.absent(MemoryModuleType.WALK_TARGET);
            return instance.group(instance.registered(MemoryModuleType.LOOK_TARGET), behaviorBuilder, instance.present(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM), instance.registered(MemoryModuleType.ITEM_PICKUP_COOLDOWN_TICKS)).apply(instance, (memoryAccessor, memoryAccessor2, memoryAccessor3, memoryAccessor4) -> {
                return (serverLevel, livingEntity, l) -> {
                    ItemEntity itemEntity = (ItemEntity)instance.get(memoryAccessor3);
                    if (instance.tryGet(memoryAccessor4).isEmpty() && predicate.test(livingEntity) && itemEntity.closerThan(livingEntity, (double)i) && livingEntity.level().getWorldBorder().isWithinBounds(itemEntity.blockPosition())) {
                        // CraftBukkit start
                        if (livingEntity instanceof Allay) {
                            EntityTargetEvent event = CraftEventFactory.callEntityTargetEvent(livingEntity, itemEntity, org.bukkit.event.entity.EntityTargetEvent.TargetReason.CLOSEST_ENTITY);

                            if (event.isCancelled()) {
                                return false;
                            }
                            if (!(event.getTarget() instanceof ItemEntity)) {
                                memoryAccessor3.erase();
                            }

                            itemEntity = (ItemEntity) ((CraftEntity) event.getTarget()).getHandle();
                        }
                        // CraftBukkit end
                        WalkTarget walkTarget = new WalkTarget(new EntityTracker(itemEntity, false), f, 0);
                        memoryAccessor.set(new EntityTracker(itemEntity, true));
                        memoryAccessor2.set(walkTarget);
                        return true;
                    } else {
                        return false;
                    }
                };
            });
        });
    }
}
