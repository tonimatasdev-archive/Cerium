package dev.tonimatas.cerium.mixins.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Iterator;

@Mixin(SculkVeinBlock.class)
public abstract class SculkVeinBlockMixin {
    @Shadow @Final private MultifaceSpreader veinSpreader;

    @Shadow public abstract void onDischarged(LevelAccessor arg, BlockState arg2, BlockPos arg3, RandomSource arg4);

    @Unique public BlockPos cerium$blockPos;

    @Inject(method = "attemptUseCharge", at = @At(value = "HEAD"))
    private void cerium$attemptUseCharge(SculkSpreader.ChargeCursor chargeCursor, LevelAccessor levelAccessor, BlockPos blockPos, RandomSource randomSource, SculkSpreader sculkSpreader, boolean bl, CallbackInfoReturnable<Integer> cir) {
        this.cerium$blockPos = blockPos;
    }

    /**
     * @author TonimatasDEV
     * @reason CraftBukkit
     */
    @Overwrite
    private boolean attemptPlaceSculk(SculkSpreader sculkSpreader, LevelAccessor levelAccessor, BlockPos blockPos, RandomSource randomSource) {
        BlockState blockState = levelAccessor.getBlockState(blockPos);
        TagKey<Block> tagKey = sculkSpreader.replaceableBlocks();
        Iterator var7 = Direction.allShuffled(randomSource).iterator();

        while(var7.hasNext()) {
            Direction direction = (Direction)var7.next();
            if (MultifaceBlock.hasFace(blockState, direction)) {
                BlockPos blockPos2 = blockPos.relative(direction);
                BlockState blockState2 = levelAccessor.getBlockState(blockPos2);
                if (blockState2.is(tagKey)) {
                    BlockState blockState3 = Blocks.SCULK.defaultBlockState();
                    // CraftBukkit start - Call BlockSpreadEvent
                    if (!org.bukkit.craftbukkit.v1_20_R3.event.CraftEventFactory.handleBlockSpreadEvent(levelAccessor, cerium$blockPos, blockPos, blockState3, 3)) {
                        return false;
                    }
                    // CraftBukkit end
                    Block.pushEntitiesUp(blockState2, blockState3, levelAccessor, blockPos2);
                    levelAccessor.playSound((Player)null, blockPos2, SoundEvents.SCULK_BLOCK_SPREAD, SoundSource.BLOCKS, 1.0F, 1.0F);
                    this.veinSpreader.spreadAll(blockState3, levelAccessor, blockPos2, sculkSpreader.isWorldGeneration());
                    Direction direction2 = direction.getOpposite();
                    Direction[] var13 = MultifaceBlock.DIRECTIONS;
                    int var14 = var13.length;

                    for(int var15 = 0; var15 < var14; ++var15) {
                        Direction direction3 = var13[var15];
                        if (direction3 != direction2) {
                            BlockPos blockPos3 = blockPos2.relative(direction3);
                            BlockState blockState4 = levelAccessor.getBlockState(blockPos3);
                            if (blockState4.is((SculkVeinBlock) (Object) this)) {
                                this.onDischarged(levelAccessor, blockState4, blockPos3, randomSource);
                            }
                        }
                    }

                    return true;
                }
            }
        }

        return false;
    }
}
