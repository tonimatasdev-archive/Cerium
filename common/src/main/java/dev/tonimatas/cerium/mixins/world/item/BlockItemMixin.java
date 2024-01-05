package dev.tonimatas.cerium.mixins.world.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PlaceOnWaterBlockItem;
import net.minecraft.world.item.SolidBucketItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.bukkit.craftbukkit.v1_20_R3.block.CraftBlock;
import org.bukkit.craftbukkit.v1_20_R3.block.data.CraftBlockData;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(BlockItem.class)
public abstract class BlockItemMixin {
    @Shadow @Nullable public abstract BlockPlaceContext updatePlacementContext(BlockPlaceContext blockPlaceContext);
    @Shadow public abstract Block getBlock();
    @Shadow protected abstract boolean placeBlock(BlockPlaceContext blockPlaceContext, BlockState blockState);
    @Shadow @Nullable protected abstract BlockState getPlacementState(BlockPlaceContext blockPlaceContext);
    @Shadow protected abstract boolean updateCustomBlockEntityTag(BlockPos blockPos, Level level, @Nullable Player player, ItemStack itemStack, BlockState blockState);

    @Shadow protected abstract boolean mustSurvive();

    /**
     * @author TonimatasDEV
     * @reason CraftBukkit
     */
    @Overwrite
    public InteractionResult place(BlockPlaceContext blockPlaceContext) {
        if (!this.getBlock().isEnabled(blockPlaceContext.getLevel().enabledFeatures())) {
            return InteractionResult.FAIL;
        } else if (!blockPlaceContext.canPlace()) {
            return InteractionResult.FAIL;
        } else {
            BlockPlaceContext blockPlaceContext2 = this.updatePlacementContext(blockPlaceContext);
            if (blockPlaceContext2 == null) {
                return InteractionResult.FAIL;
            } else {
                BlockState blockState = this.getPlacementState(blockPlaceContext2);

                // CraftBukkit start - special case for handling block placement with water lilies and snow buckets
                org.bukkit.block.BlockState blockstate = null;
                if ((BlockItem) (Object) this instanceof PlaceOnWaterBlockItem || (BlockItem) (Object) this instanceof SolidBucketItem) {
                    blockstate = org.bukkit.craftbukkit.v1_20_R3.block.CraftBlockStates.getBlockState(blockPlaceContext2.getLevel(), blockPlaceContext2.getClickedPos());
                }
                // CraftBukkit end

                if (blockState == null) {
                    return InteractionResult.FAIL;
                } else if (!this.placeBlock(blockPlaceContext2, blockState)) {
                    return InteractionResult.FAIL;
                } else {
                    BlockPos blockPos = blockPlaceContext2.getClickedPos();
                    Level level = blockPlaceContext2.getLevel();
                    Player player = blockPlaceContext2.getPlayer();
                    ItemStack itemStack = blockPlaceContext2.getItemInHand();
                    BlockState blockState2 = level.getBlockState(blockPos);
                    if (blockState2.is(blockState.getBlock())) {
                        blockState2 = this.updateBlockStateFromTag(blockPos, level, itemStack, blockState2);
                        this.updateCustomBlockEntityTag(blockPos, level, player, itemStack, blockState2);
                        blockState2.getBlock().setPlacedBy(level, blockPos, blockState2, player, itemStack);
                        // CraftBukkit start
                        if (blockstate != null) {
                            org.bukkit.event.block.BlockPlaceEvent placeEvent = org.bukkit.craftbukkit.v1_20_R3.event.CraftEventFactory.callBlockPlaceEvent((ServerLevel) level, player, blockPlaceContext2.getHand(), blockstate, blockPos.getX(), blockPos.getY(), blockPos.getZ());
                            if (placeEvent != null && (placeEvent.isCancelled() || !placeEvent.canBuild())) {
                                blockstate.update(true, false);

                                if ((BlockItem) (Object) this instanceof SolidBucketItem) {
                                    ((ServerPlayer) player).getBukkitEntity().updateInventory(); // SPIGOT-4541
                                }
                                return InteractionResult.FAIL;
                            }
                        }
                        // CraftBukkit end
                        if (player instanceof ServerPlayer) {
                            CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer)player, blockPos, itemStack);
                        }
                    }

                    SoundType soundType = blockState2.getSoundType();
                    // level.playSound(player, blockPos, this.getPlaceSound(blockState2), SoundSource.BLOCKS, (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F);
                    level.gameEvent(GameEvent.BLOCK_PLACE, blockPos, GameEvent.Context.of(player, blockState2));
                    if ((player == null || !player.getAbilities().instabuild) && itemStack != ItemStack.EMPTY) { // CraftBukkit
                        itemStack.shrink(1);
                    }

                    return InteractionResult.sidedSuccess(level.isClientSide);
                }
            }
        }
    }

    /**
     * @author TonimatasDEV
     * @reason CraftBukkit
     */
    @Overwrite
    private BlockState updateBlockStateFromTag(BlockPos blockPos, Level level, ItemStack itemStack, BlockState blockState) {
        BlockState blockState2 = blockState;
        CompoundTag compoundTag = itemStack.getTag();
        if (compoundTag != null) {
            CompoundTag compoundTag2 = compoundTag.getCompound("BlockStateTag");
            // CraftBukkit start
            blockState2 = getBlockState(blockState2, compoundTag2);
        }

        if (blockState2 != blockState) {
            level.setBlock(blockPos, blockState2, 2);
        }

        return blockState2;
    }

    @Unique
    private static BlockState getBlockState(BlockState blockState, CompoundTag compoundTag) {
        StateDefinition<Block, BlockState> statecontainer = blockState.getBlock().getStateDefinition();
        for (String s : compoundTag.getAllKeys()) {
            Property<?> iproperty = statecontainer.getProperty(s);
            if (iproperty != null) {
                String s1 = compoundTag.get(s).getAsString();
                blockState = BlockItem.updateState(blockState, iproperty, s1);
            }
        }
        return blockState;
    }
    // CraftBukkit end

    /**
     * @author TonimatasDEV
     * @reason CraftBukkit
     */
    @Overwrite
    protected boolean canPlace(BlockPlaceContext blockPlaceContext, BlockState blockState) {
        Player player = blockPlaceContext.getPlayer();
        CollisionContext collisionContext = player == null ? CollisionContext.empty() : CollisionContext.of(player);

        boolean defaultReturn = (!this.mustSurvive() || blockState.canSurvive(blockPlaceContext.getLevel(), blockPlaceContext.getClickedPos())) && blockPlaceContext.getLevel().isUnobstructed(blockState, blockPlaceContext.getClickedPos(), collisionContext);
        org.bukkit.entity.Player bukkitPlayer = (blockPlaceContext.getPlayer() instanceof ServerPlayer) ? (org.bukkit.entity.Player) player.getBukkitEntity() : null;
        BlockCanBuildEvent event = new BlockCanBuildEvent(CraftBlock.at(blockPlaceContext.getLevel(), blockPlaceContext.getClickedPos()), bukkitPlayer, CraftBlockData.fromData(blockState), defaultReturn);
        blockPlaceContext.getLevel().getCraftServer().getPluginManager().callEvent(event);

        return event.isBuildable();
    }
}
