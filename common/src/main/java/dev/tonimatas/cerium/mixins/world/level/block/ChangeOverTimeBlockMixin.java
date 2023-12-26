package dev.tonimatas.cerium.mixins.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.ChangeOverTimeBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Optional;

@Mixin(ChangeOverTimeBlock.class)
public interface ChangeOverTimeBlockMixin {
    @Shadow Optional<BlockState> getNextState(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource randomSource);

    /**
     * @author TonimatasDEV
     * @reason CraftBukkit
     */
    @Overwrite
    default void changeOverTime(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource randomSource) {
        float f = 0.05688889F;
        if (randomSource.nextFloat() < 0.05688889F) {
            this.getNextState(blockState, serverLevel, blockPos, randomSource).ifPresent((blockStatex) -> {
                org.bukkit.craftbukkit.v1_20_R3.event.CraftEventFactory.handleBlockFormEvent(serverLevel, blockPos, blockStatex);
            });
        }

    }
}
