package org.bukkit.craftbukkit.v1_20_R3.block;

import net.minecraft.world.level.block.entity.BlockEntityBlastFurnace;
import org.bukkit.World;
import org.bukkit.block.BlastFurnace;

public class CraftBlastFurnace extends CraftFurnace<BlockEntityBlastFurnace> implements BlastFurnace {

    public CraftBlastFurnace(World world, BlockEntityBlastFurnace tileEntity) {
        super(world, tileEntity);
    }

    protected CraftBlastFurnace(CraftBlastFurnace state) {
        super(state);
    }

    @Override
    public CraftBlastFurnace copy() {
        return new CraftBlastFurnace(this);
    }
}
