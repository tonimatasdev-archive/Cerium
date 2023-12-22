package org.bukkit.craftbukkit.v1_20_R3.block;

import net.minecraft.world.level.block.entity.BlockEntityConduit;
import org.bukkit.World;
import org.bukkit.block.Conduit;

public class CraftConduit extends CraftBlockEntityState<BlockEntityConduit> implements Conduit {

    public CraftConduit(World world, BlockEntityConduit tileEntity) {
        super(world, tileEntity);
    }

    protected CraftConduit(CraftConduit state) {
        super(state);
    }

    @Override
    public CraftConduit copy() {
        return new CraftConduit(this);
    }
}
