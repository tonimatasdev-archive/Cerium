package org.bukkit.craftbukkit.v1_20_R3.block;

import net.minecraft.world.level.block.entity.BlockEntityFurnaceFurnace;
import org.bukkit.World;

public class CraftFurnaceFurnace extends CraftFurnace<BlockEntityFurnaceFurnace> {

    public CraftFurnaceFurnace(World world, BlockEntityFurnaceFurnace tileEntity) {
        super(world, tileEntity);
    }

    protected CraftFurnaceFurnace(CraftFurnaceFurnace state) {
        super(state);
    }

    @Override
    public CraftFurnaceFurnace copy() {
        return new CraftFurnaceFurnace(this);
    }
}
