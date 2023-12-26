package dev.tonimatas.cerium.mixins.world.level.block;

import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractCandleBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.bukkit.craftbukkit.v1_20_R3.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractCandleBlock.class)
public class AbstractCandleBlockMixin {
    @Inject(method = "onProjectileHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/AbstractCandleBlock;setLit(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Z)V", shift = At.Shift.BEFORE))
    private void cerium$onProjectileHit(Level level, BlockState blockState, BlockHitResult blockHitResult, Projectile projectile, CallbackInfo ci) {
        if (CraftEventFactory.callBlockIgniteEvent(level, blockHitResult.getBlockPos(), projectile).isCancelled()) {
            return;
        }
    }
}
