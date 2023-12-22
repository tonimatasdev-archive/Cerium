package org.bukkit.craftbukkit.v1_20_R3.block;

import net.minecraft.world.level.block.BlockDropper;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityDropper;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Dropper;
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftInventory;
import org.bukkit.inventory.Inventory;

public class CraftDropper extends CraftLootable<BlockEntityDropper> implements Dropper {

    public CraftDropper(World world, BlockEntityDropper tileEntity) {
        super(world, tileEntity);
    }

    protected CraftDropper(CraftDropper state) {
        super(state);
    }

    @Override
    public Inventory getSnapshotInventory() {
        return new CraftInventory(this.getSnapshot());
    }

    @Override
    public Inventory getInventory() {
        if (!this.isPlaced()) {
            return this.getSnapshotInventory();
        }

        return new CraftInventory(this.getBlockEntity());
    }

    @Override
    public void drop() {
        ensureNoWorldGeneration();
        Block block = getBlock();
        if (block.getType() == Material.DROPPER) {
            CraftWorld world = (CraftWorld) this.getWorld();
            BlockDropper drop = (BlockDropper) Blocks.DROPPER;

            drop.dispenseFrom(world.getHandle(), this.getHandle(), this.getPosition());
        }
    }

    @Override
    public CraftDropper copy() {
        return new CraftDropper(this);
    }
}
