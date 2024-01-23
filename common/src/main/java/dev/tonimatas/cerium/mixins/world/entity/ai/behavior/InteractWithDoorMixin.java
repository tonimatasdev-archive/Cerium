package dev.tonimatas.cerium.mixins.world.entity.ai.behavior;

import dev.tonimatas.cerium.bridge.world.level.LevelBridge;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.InteractWithDoor;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.mutable.MutableObject;
import org.bukkit.craftbukkit.v1_20_R3.block.CraftBlock;
import org.bukkit.event.entity.EntityInteractEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Mixin(InteractWithDoor.class)
public class InteractWithDoorMixin {
    /**
     * @author TonimatasDEV
     * @reason CraftBukkit
     */
    @Overwrite
    public static BehaviorControl<LivingEntity> create() {
        MutableObject<Node> mutableObject = new MutableObject((Object)null);
        MutableInt mutableInt = new MutableInt(0);
        return BehaviorBuilder.create((instance) -> {
            return instance.group(instance.present(MemoryModuleType.PATH), instance.registered(MemoryModuleType.DOORS_TO_CLOSE), instance.registered(MemoryModuleType.NEAREST_LIVING_ENTITIES)).apply(instance, (memoryAccessor, memoryAccessor2, memoryAccessor3) -> {
                return (serverLevel, livingEntity, l) -> {
                    Path path = (Path)instance.get(memoryAccessor);
                    Optional<Set<GlobalPos>> optional = instance.tryGet(memoryAccessor2);
                    if (!path.notStarted() && !path.isDone()) {
                        if (Objects.equals(mutableObject.getValue(), path.getNextNode())) {
                            mutableInt.setValue(20);
                        } else if (mutableInt.decrementAndGet() > 0) {
                            return false;
                        }

                        mutableObject.setValue(path.getNextNode());
                        Node node = path.getPreviousNode();
                        Node node2 = path.getNextNode();
                        BlockPos blockPos = node.asBlockPos();
                        BlockState blockState = serverLevel.getBlockState(blockPos);
                        if (blockState.is(BlockTags.WOODEN_DOORS, (blockStateBase) -> {
                            return blockStateBase.getBlock() instanceof DoorBlock;net.minecraft.world.entity.ai.behavior.HarvestFarmland
                        })) {
                            DoorBlock doorBlock = (DoorBlock)blockState.getBlock();
                            if (!doorBlock.isOpen(blockState)) {
                                // CraftBukkit start - entities opening doors
                                EntityInteractEvent event = new EntityInteractEvent(livingEntity.getBukkitEntity(), CraftBlock.at(livingEntity.level(), blockPos));
                                ((LevelBridge) livingEntity.level()).getCraftServer().getPluginManager().callEvent(event);
                                if (event.isCancelled()) {
                                    return false;
                                }
                                // CraftBukkit end
                                doorBlock.setOpen(livingEntity, serverLevel, blockState, blockPos, true);
                            }

                            optional = InteractWithDoor.rememberDoorToClose(memoryAccessor2, optional, serverLevel, blockPos);
                        }

                        BlockPos blockPos2 = node2.asBlockPos();
                        BlockState blockState2 = serverLevel.getBlockState(blockPos2);
                        if (blockState2.is(BlockTags.WOODEN_DOORS, (blockStateBase) -> {
                            return blockStateBase.getBlock() instanceof DoorBlock;
                        })) {
                            DoorBlock doorBlock2 = (DoorBlock)blockState2.getBlock();
                            if (!doorBlock2.isOpen(blockState2)) {
                                // CraftBukkit start - entities opening doors
                                EntityInteractEvent event = new EntityInteractEvent(livingEntity.getBukkitEntity(), CraftBlock.at(livingEntity.level(), blockPos2));
                                ((LevelBridge) livingEntity.level()).getCraftServer().getPluginManager().callEvent(event);
                                if (event.isCancelled()) {
                                    return false;
                                }
                                // CraftBukkit end
                                doorBlock2.setOpen(livingEntity, serverLevel, blockState2, blockPos2, true);
                                optional = InteractWithDoor.rememberDoorToClose(memoryAccessor2, optional, serverLevel, blockPos2);
                            }
                        }

                        optional.ifPresent((set) -> {
                            InteractWithDoor.closeDoorsThatIHaveOpenedOrPassedThrough(serverLevel, livingEntity, node, node2, set, instance.tryGet(memoryAccessor3));
                        });
                        return true;
                    } else {
                        return false;
                    }
                };
            });
        });
    }
}
