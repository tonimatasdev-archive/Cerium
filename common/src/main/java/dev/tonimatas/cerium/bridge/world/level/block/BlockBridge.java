package dev.tonimatas.cerium.bridge.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public interface BlockBridge {
    int tryDropExperience(ServerLevel serverLevel, BlockPos blockPos, ItemStack itemStack, IntProvider intProvider);
    
    int getExpDrop(BlockState iblockdata, ServerLevel worldserver, BlockPos blockposition, ItemStack itemstack, boolean flag);
}
