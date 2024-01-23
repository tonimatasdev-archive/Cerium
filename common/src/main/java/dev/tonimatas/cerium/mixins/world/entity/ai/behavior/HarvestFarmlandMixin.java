package dev.tonimatas.cerium.mixins.world.entity.ai.behavior;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.WrapWithCondition;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.behavior.BlockPosTracker;
import net.minecraft.world.entity.ai.behavior.HarvestFarmland;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.bukkit.craftbukkit.v1_20_R3.event.CraftEventFactory;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(HarvestFarmland.class)
public abstract class HarvestFarmlandMixin {
    @Shadow @Nullable private BlockPos aboveFarmlandPos;

    @Shadow private long nextOkStartTime;

    @Shadow @Final private List<BlockPos> validFarmlandAroundVillager;

    @Shadow @Nullable protected abstract BlockPos getValidFarmland(ServerLevel serverLevel);

    @Shadow private int timeWorkedSoFar;

    // TODO: Add forge things.
    
    
    /**
     * @author TonimatasDEV
     * @reason CraftBukkit
     */
    @Overwrite
    protected void tick(ServerLevel serverLevel, Villager villager, long l) {
        if (this.aboveFarmlandPos == null || this.aboveFarmlandPos.closerToCenterThan(villager.position(), 1.0)) {
            if (this.aboveFarmlandPos != null && l > this.nextOkStartTime) {
                BlockState blockState = serverLevel.getBlockState(this.aboveFarmlandPos);
                Block block = blockState.getBlock();
                Block block2 = serverLevel.getBlockState(this.aboveFarmlandPos.below()).getBlock();
                if (block instanceof CropBlock && ((CropBlock)block).isMaxAge(blockState)) {
                    if (CraftEventFactory.callEntityChangeBlockEvent(villager, this.aboveFarmlandPos, Blocks.AIR.defaultBlockState())) { // CraftBukkit
                        serverLevel.destroyBlock(this.aboveFarmlandPos, true, villager);
                    } // CraftBukkit
                }

                if (blockState.isAir() && block2 instanceof FarmBlock && villager.hasFarmSeeds()) {
                    SimpleContainer simpleContainer = villager.getInventory();

                    for(int i = 0; i < simpleContainer.getContainerSize(); ++i) {
                        ItemStack itemStack = simpleContainer.getItem(i);
                        boolean bl = false;
                        if (!itemStack.isEmpty() && itemStack.is(ItemTags.VILLAGER_PLANTABLE_SEEDS)) {
                            Item var13 = itemStack.getItem();
                            if (var13 instanceof BlockItem) {
                                BlockItem blockItem = (BlockItem)var13;
                                BlockState blockState2 = blockItem.getBlock().defaultBlockState();
                                if (CraftEventFactory.callEntityChangeBlockEvent(villager, this.aboveFarmlandPos, blockState2)) { // CraftBukkit
                                serverLevel.setBlockAndUpdate(this.aboveFarmlandPos, blockState2);
                                serverLevel.gameEvent(GameEvent.BLOCK_PLACE, this.aboveFarmlandPos, GameEvent.Context.of(villager, blockState2));
                                bl = true;
                                } // CraftBukkit
                            }
                        }

                        if (bl) {
                            serverLevel.playSound((Player)null, (double)this.aboveFarmlandPos.getX(), (double)this.aboveFarmlandPos.getY(), (double)this.aboveFarmlandPos.getZ(), SoundEvents.CROP_PLANTED, SoundSource.BLOCKS, 1.0F, 1.0F);
                            itemStack.shrink(1);
                            if (itemStack.isEmpty()) {
                                simpleContainer.setItem(i, ItemStack.EMPTY);
                            }
                            break;
                        }
                    }
                }

                if (block instanceof CropBlock && !((CropBlock)block).isMaxAge(blockState)) {
                    this.validFarmlandAroundVillager.remove(this.aboveFarmlandPos);
                    this.aboveFarmlandPos = this.getValidFarmland(serverLevel);
                    if (this.aboveFarmlandPos != null) {
                        this.nextOkStartTime = l + 20L;
                        villager.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(new BlockPosTracker(this.aboveFarmlandPos), 0.5F, 1));
                        villager.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new BlockPosTracker(this.aboveFarmlandPos));
                    }
                }
            }

            ++this.timeWorkedSoFar;
        }
    }
}
