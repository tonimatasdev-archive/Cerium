package org.bukkit.craftbukkit.v1_20_R3.block;

import net.minecraft.world.level.block.entity.BlockEntityJigsaw;
import org.bukkit.World;
import org.bukkit.block.Jigsaw;

public class CraftJigsaw extends CraftBlockEntityState<BlockEntityJigsaw> implements Jigsaw {

    public CraftJigsaw(World world, BlockEntityJigsaw tileEntity) {
        super(world, tileEntity);
    }

    protected CraftJigsaw(CraftJigsaw state) {
        super(state);
    }

    @Override
    public CraftJigsaw copy() {
        return new CraftJigsaw(this);
    }
}
