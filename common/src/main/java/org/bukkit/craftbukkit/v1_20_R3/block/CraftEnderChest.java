package org.bukkit.craftbukkit.v1_20_R3.block;

import net.minecraft.world.level.block.entity.EnderChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.World;
import org.bukkit.block.EnderChest;

public class CraftEnderChest extends CraftBlockEntityState<EnderChestBlockEntity> implements EnderChest {

    public CraftEnderChest(World world, EnderChestBlockEntity tileEntity) {
        super(world, tileEntity);
    }

    protected CraftEnderChest(CraftEnderChest state) {
        super(state);
    }

    @Override
    public void open() {
        requirePlaced();
        if (!getBlockEntity().openersCounter.opened && getWorldHandle() instanceof net.minecraft.world.level.Level) {
            BlockState block = getBlockEntity().getBlockState();
            int openCount = getBlockEntity().openersCounter.getOpenerCount();

            getBlockEntity().openersCounter.onAPIOpen((net.minecraft.world.level.Level) getWorldHandle(), getPosition(), block);
            getBlockEntity().openersCounter.openerAPICountChanged((net.minecraft.world.level.Level) getWorldHandle(), getPosition(), block, openCount, openCount + 1);
        }
        getBlockEntity().openersCounter.opened = true;
    }

    @Override
    public void close() {
        requirePlaced();
        if (getBlockEntity().openersCounter.opened && getWorldHandle() instanceof net.minecraft.world.level.Level) {
            BlockState block = getBlockEntity().getBlockState();
            int openCount = getBlockEntity().openersCounter.getOpenerCount();

            getBlockEntity().openersCounter.onAPIClose((net.minecraft.world.level.Level) getWorldHandle(), getPosition(), block);
            getBlockEntity().openersCounter.openerAPICountChanged((net.minecraft.world.level.Level) getWorldHandle(), getPosition(), block, openCount, 0);
        }
        getBlockEntity().openersCounter.opened = false;
    }

    @Override
    public CraftEnderChest copy() {
        return new CraftEnderChest(this);
    }
}
