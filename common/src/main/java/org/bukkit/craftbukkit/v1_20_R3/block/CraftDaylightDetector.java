package org.bukkit.craftbukkit.v1_20_R3.block;

import net.minecraft.world.level.block.entity.BlockEntityLightDetector;
import org.bukkit.World;
import org.bukkit.block.DaylightDetector;

public class CraftDaylightDetector extends CraftBlockEntityState<BlockEntityLightDetector> implements DaylightDetector {

    public CraftDaylightDetector(World world, BlockEntityLightDetector tileEntity) {
        super(world, tileEntity);
    }

    protected CraftDaylightDetector(CraftDaylightDetector state) {
        super(state);
    }

    @Override
    public CraftDaylightDetector copy() {
        return new CraftDaylightDetector(this);
    }
}
