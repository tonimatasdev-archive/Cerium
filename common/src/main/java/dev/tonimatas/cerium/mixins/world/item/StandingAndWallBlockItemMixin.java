package dev.tonimatas.cerium.mixins.world.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.bukkit.craftbukkit.v1_20_R3.block.CraftBlock;
import org.bukkit.craftbukkit.v1_20_R3.block.data.CraftBlockData;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(StandingAndWallBlockItem.class)
public abstract class StandingAndWallBlockItemMixin {
    @Shadow @Final private Direction attachmentDirection;

    @Shadow @Final protected Block wallBlock;

    @Shadow protected abstract boolean canPlace(LevelReader levelReader, BlockState blockState, BlockPos blockPos);

    /**
     * @author TonimatasDEV
     * @reason CraftBukkit
     */
    @Overwrite
    @Nullable
    protected BlockState getPlacementState(BlockPlaceContext blockPlaceContext) {
        BlockState blockState = this.wallBlock.getStateForPlacement(blockPlaceContext);
        BlockState blockState2 = null;
        Level levelReader = blockPlaceContext.getLevel(); // Cerium LevelReader to Level
        BlockPos blockPos = blockPlaceContext.getClickedPos();
        Direction[] var6 = blockPlaceContext.getNearestLookingDirections();
        int var7 = var6.length;

        for(int var8 = 0; var8 < var7; ++var8) {
            Direction direction = var6[var8];
            if (direction != this.attachmentDirection.getOpposite()) {
                BlockState blockState3 = direction == this.attachmentDirection ? ((BlockItem) (Object) this).getBlock().getStateForPlacement(blockPlaceContext) : blockState;
                if (blockState3 != null && this.canPlace(levelReader, blockState3, blockPos)) {
                    blockState2 = blockState3;
                    break;
                }
            }
        }

        // CraftBukkit start
        if (blockState2 != null) {
            boolean defaultReturn = levelReader.isUnobstructed(blockState2, blockPos, CollisionContext.empty());
            org.bukkit.entity.Player player = (blockPlaceContext.getPlayer() instanceof ServerPlayer) ? (org.bukkit.entity.Player) blockPlaceContext.getPlayer().getBukkitEntity() : null;

            BlockCanBuildEvent event = new BlockCanBuildEvent(CraftBlock.at(levelReader, blockPos), player, CraftBlockData.fromData(blockState2), defaultReturn);
            blockPlaceContext.getLevel().getCraftServer().getPluginManager().callEvent(event);

            return (event.isBuildable()) ? blockState2 : null;
        } else {
            return null;
        }
        // CraftBukkit end
    }
}
