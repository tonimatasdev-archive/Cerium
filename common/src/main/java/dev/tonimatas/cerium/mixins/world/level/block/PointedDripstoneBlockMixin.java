package dev.tonimatas.cerium.mixins.world.level.block;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.bukkit.craftbukkit.v1_20_R3.block.CraftBlock;
import org.bukkit.craftbukkit.v1_20_R3.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PointedDripstoneBlock.class)
public abstract class PointedDripstoneBlockMixin {
    @Unique
    private static BlockPos cerium$source;

    @Inject(method = "onProjectileHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;destroyBlock(Lnet/minecraft/core/BlockPos;Z)Z", shift = At.Shift.BEFORE), cancellable = true)
    private void cerium$onProjectileHit(Level level, BlockState blockState, BlockHitResult blockHitResult, Projectile projectile, CallbackInfo ci, @Local BlockPos blockPos) {
        if (!CraftEventFactory.callEntityChangeBlockEvent(projectile, blockPos, Blocks.AIR.defaultBlockState())) {
            ci.cancel();
        }
    }

    @Redirect(method = "fallOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;causeFallDamage(FFLnet/minecraft/world/damagesource/DamageSource;)Z"))
    private boolean cerium$fallON(Entity instance, float f, float g, DamageSource damageSource, @Local Level level, @Local BlockPos blockPos) {
        CraftEventFactory.blockDamage = CraftBlock.at(level, blockPos);
        boolean b = instance.causeFallDamage(f + 2.0F, 2.0F, level.damageSources().stalagmite());
        CraftEventFactory.blockDamage = null;
        return b;
    }

    @Inject(method = "grow", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/PointedDripstoneBlock;createDripstone(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;Lnet/minecraft/world/level/block/state/properties/DripstoneThickness;)V", shift = At.Shift.BEFORE))
    private static void cerium$grow(ServerLevel serverLevel, BlockPos blockPos, Direction direction, CallbackInfo ci) {
        cerium$source = blockPos;
    }

    @Redirect(method = "createDripstone", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/LevelAccessor;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private static boolean cerium$createDripstone(LevelAccessor instance, BlockPos blockPos, BlockState blockState, int i) {
        return org.bukkit.craftbukkit.v1_20_R3.event.CraftEventFactory.handleBlockSpreadEvent(instance, cerium$source, blockPos, blockState, 3); // CraftBukkit
    }

    @Inject(method = "createMergedTips", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/PointedDripstoneBlock;createDripstone(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;Lnet/minecraft/world/level/block/state/properties/DripstoneThickness;)V", shift = At.Shift.BEFORE))
    private static void cerium$createMergedTips(BlockState blockState, LevelAccessor levelAccessor, BlockPos blockPos, CallbackInfo ci) {
        cerium$source = blockPos;
    }
}
