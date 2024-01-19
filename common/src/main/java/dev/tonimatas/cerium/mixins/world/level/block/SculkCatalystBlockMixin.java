package dev.tonimatas.cerium.mixins.world.level.block;

import dev.tonimatas.cerium.bridge.world.level.block.BlockBridge;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.SculkCatalystBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SculkCatalystBlock.class)
public abstract class SculkCatalystBlockMixin implements BlockBridge {

    @Inject(method = "spawnAfterBreak", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/BaseEntityBlock;spawnAfterBreak(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/item/ItemStack;Z)V", shift = At.Shift.AFTER), cancellable = true)
    private void cerium$spawnAfterBreak(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, ItemStack itemStack, boolean bl, CallbackInfo ci) {
        ci.cancel();
    }
    
    @Override
    public int getExpDrop(BlockState iblockdata, ServerLevel worldserver, BlockPos blockposition, ItemStack itemstack, boolean flag) {
        if (flag) {
            return this.tryDropExperience(worldserver, blockposition, itemstack, ConstantInt.of(5));
        }
        
        return 0;
        // CraftBukkit end
    }
}
