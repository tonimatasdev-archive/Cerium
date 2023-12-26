package dev.tonimatas.cerium.mixins.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.IceBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.v1_20_R3.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(IceBlock.class)
public class IceBlockMixin {
    @Inject(method = "melt", at = @At(value = "HEAD"), cancellable = true)
    private void cerium$melt(BlockState blockState, Level level, BlockPos blockPos, CallbackInfo ci) {
        if (CraftEventFactory.callBlockFadeEvent(level, blockPos, level.dimensionType().ultraWarm() ? Blocks.AIR.defaultBlockState() : Blocks.WATER.defaultBlockState()).isCancelled()) {
            ci.cancel();
        }
    }
}
