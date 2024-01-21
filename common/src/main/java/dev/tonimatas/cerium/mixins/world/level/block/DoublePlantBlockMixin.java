package dev.tonimatas.cerium.mixins.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.v1_20_R3.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DoublePlantBlock.class)
public class DoublePlantBlockMixin {
    @Inject(method = "preventDropFromBottomPart", at = @At(value = "HEAD"), cancellable = true)
    private static void cerium$preventDropFromBottomPart(Level level, BlockPos blockPos, BlockState blockState, Player player, CallbackInfo ci) {
        // CraftBukkit start
        if (CraftEventFactory.callBlockPhysicsEvent(level, blockPos).isCancelled()) {
            ci.cancel();
        }
        // CraftBukkit end
    }
}
