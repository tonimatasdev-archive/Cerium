package org.bukkit.craftbukkit.v1_20_R3.block;

import net.minecraft.world.level.block.entity.BlockEntitySmoker;
import org.bukkit.World;
import org.bukkit.block.Smoker;

public class CraftSmoker extends CraftFurnace<BlockEntitySmoker> implements Smoker {

    public CraftSmoker(World world, BlockEntitySmoker tileEntity) {
        super(world, tileEntity);
    }

    protected CraftSmoker(CraftSmoker state) {
        super(state);
    }

    @Override
    public CraftSmoker copy() {
        return new CraftSmoker(this);
    }
}
