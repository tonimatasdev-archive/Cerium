package dev.tonimatas.cerium.mixins.world.level.redstone;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.NeighborUpdater;
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R3.block.CraftBlock;
import org.bukkit.craftbukkit.v1_20_R3.block.data.CraftBlockData;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NeighborUpdater.class)
public class NeighborUpdaterMixin {
    @Inject(method = "executeUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;neighborChanged(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Block;Lnet/minecraft/core/BlockPos;Z)V"))
    private static void cerium$executeUpdate(Level level, BlockState blockState, BlockPos blockPos, Block block, BlockPos blockPos2, boolean bl, CallbackInfo ci) {
        // CraftBukkit start
        CraftWorld cworld = ((ServerLevel) level).getWorld();
        if (cworld != null) {
            BlockPhysicsEvent event = new BlockPhysicsEvent(CraftBlock.at(level, blockPos), CraftBlockData.fromData(blockState), CraftBlock.at(level, blockPos2));
            ((ServerLevel) level).getCraftServer().getPluginManager().callEvent(event);

            if (event.isCancelled()) {
                return;
            }
        }
        // CraftBukkit end
    }
}
