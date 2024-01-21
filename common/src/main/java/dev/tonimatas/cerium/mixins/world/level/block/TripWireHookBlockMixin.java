package dev.tonimatas.cerium.mixins.world.level.block;

import dev.tonimatas.cerium.bridge.world.level.LevelBridge;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.TripWireHookBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.v1_20_R3.block.CraftBlock;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TripWireHookBlock.class)
public class TripWireHookBlockMixin {
    @Inject(method = "calculateState", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/TripWireHookBlock;emitState(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;ZZZZ)V", ordinal = 1))
    private static void cerium$calculateState(Level level, BlockPos blockPos, BlockState blockState, boolean bl, boolean bl2, int i, BlockState blockState2, CallbackInfo ci) {
        // CraftBukkit start
        BlockRedstoneEvent eventRedstone = new BlockRedstoneEvent(CraftBlock.at(level, blockPos), 15, 0);
        ((LevelBridge) level).getCraftServer().getPluginManager().callEvent(eventRedstone);

        if (eventRedstone.getNewCurrent() > 0) {
            return;
        }
        // CraftBukkit end
    }
}
