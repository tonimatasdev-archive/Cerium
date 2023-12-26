package dev.tonimatas.cerium.mixins.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.event.block.LeavesDecayEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LeavesBlock.class)
public class LeavesBlockMixin {
    @SuppressWarnings("ConstantValue")
    @Inject(method = "randomTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/LeavesBlock;dropResources(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)V", shift = At.Shift.BEFORE), cancellable = true)
    private void cerium$randomTick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource randomSource, CallbackInfo ci) {
        LeavesDecayEvent event = new LeavesDecayEvent(serverLevel.getWorld().getBlockAt(blockPos.getX(), blockPos.getY(), blockPos.getZ()));
        serverLevel.getCraftServer().getPluginManager().callEvent(event);

        if (event.isCancelled() || serverLevel.getBlockState(blockPos).getBlock() != (Object) this) {
           ci.cancel();
        }
    }
}
