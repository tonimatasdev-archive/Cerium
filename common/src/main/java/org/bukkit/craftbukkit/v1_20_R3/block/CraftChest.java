package org.bukkit.craftbukkit.v1_20_R3.block;

import net.minecraft.world.ITileInventory;
import net.minecraft.world.level.block.BlockChest;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityChest;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Chest;
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftInventory;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftInventoryDoubleChest;
import org.bukkit.inventory.Inventory;

public class CraftChest extends CraftLootable<BlockEntityChest> implements Chest {

    public CraftChest(World world, BlockEntityChest tileEntity) {
        super(world, tileEntity);
    }

    protected CraftChest(CraftChest state) {
        super(state);
    }

    @Override
    public Inventory getSnapshotInventory() {
        return new CraftInventory(this.getSnapshot());
    }

    @Override
    public Inventory getBlockInventory() {
        if (!this.isPlaced()) {
            return this.getSnapshotInventory();
        }

        return new CraftInventory(this.getBlockEntity());
    }

    @Override
    public Inventory getInventory() {
        CraftInventory inventory = (CraftInventory) this.getBlockInventory();
        if (!isPlaced() || isWorldGeneration()) {
            return inventory;
        }

        // The logic here is basically identical to the logic in BlockChest.interact
        CraftWorld world = (CraftWorld) this.getWorld();

        BlockChest blockChest = (BlockChest) (this.getType() == Material.CHEST ? Blocks.CHEST : Blocks.TRAPPED_CHEST);
        ITileInventory nms = blockChest.getMenuProvider(data, world.getHandle(), this.getPosition(), true);

        if (nms instanceof BlockChest.DoubleInventory) {
            inventory = new CraftInventoryDoubleChest((BlockChest.DoubleInventory) nms);
        }
        return inventory;
    }

    @Override
    public void open() {
        requirePlaced();
        if (!getBlockEntity().openersCounter.opened && getWorldHandle() instanceof net.minecraft.world.level.Level) {
            IBlockData block = getBlockEntity().getBlockState();
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
            IBlockData block = getBlockEntity().getBlockState();
            int openCount = getBlockEntity().openersCounter.getOpenerCount();

            getBlockEntity().openersCounter.onAPIClose((net.minecraft.world.level.Level) getWorldHandle(), getPosition(), block);
            getBlockEntity().openersCounter.openerAPICountChanged((net.minecraft.world.level.Level) getWorldHandle(), getPosition(), block, openCount, 0);
        }
        getBlockEntity().openersCounter.opened = false;
    }

    @Override
    public CraftChest copy() {
        return new CraftChest(this);
    }
}
