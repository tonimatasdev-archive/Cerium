package org.bukkit.craftbukkit.v1_20_R3.block;

import net.minecraft.world.level.block.entity.BlockEntityEnderPortal;
import org.bukkit.World;

public class CraftEndPortal extends CraftBlockEntityState<BlockEntityEnderPortal> {

    public CraftEndPortal(World world, BlockEntityEnderPortal tileEntity) {
        super(world, tileEntity);
    }

    protected CraftEndPortal(CraftEndPortal state) {
        super(state);
    }

    @Override
    public CraftEndPortal copy() {
        return new CraftEndPortal(this);
    }
}
