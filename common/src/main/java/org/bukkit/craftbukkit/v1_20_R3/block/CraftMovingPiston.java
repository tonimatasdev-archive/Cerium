package org.bukkit.craftbukkit.v1_20_R3.block;

import net.minecraft.world.level.block.piston.BlockEntityPiston;
import org.bukkit.World;

public class CraftMovingPiston extends CraftBlockEntityState<BlockEntityPiston> {

    public CraftMovingPiston(World world, BlockEntityPiston tileEntity) {
        super(world, tileEntity);
    }

    protected CraftMovingPiston(CraftMovingPiston state) {
        super(state);
    }

    @Override
    public CraftMovingPiston copy() {
        return new CraftMovingPiston(this);
    }
}
