package org.bukkit.craftbukkit.v1_20_R3.util;

import net.minecraft.core.BaseBlockPos;
import net.minecraft.core.BlockPos;
import org.bukkit.util.BlockVector;

public final class CraftBlockVector {

    private CraftBlockVector() {
    }

    public static BlockPos toBlockPos(BlockVector blockVector) {
        return new BlockPos(blockVector.getBlockX(), blockVector.getBlockY(), blockVector.getBlockZ());
    }

    public static BlockVector toBukkit(BaseBlockPos baseBlockPos) {
        return new BlockVector(baseBlockPos.getX(), baseBlockPos.getY(), baseBlockPos.getZ());
    }
}
