package org.bukkit.craftbukkit.v1_20_R3.block;

import net.minecraft.world.level.block.BlockDispenser;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityDispenser;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Dispenser;
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftInventory;
import org.bukkit.craftbukkit.v1_20_R3.projectiles.CraftBlockProjectileSource;
import org.bukkit.inventory.Inventory;
import org.bukkit.projectiles.BlockProjectileSource;

public class CraftDispenser extends CraftLootable<BlockEntityDispenser> implements Dispenser {

    public CraftDispenser(World world, BlockEntityDispenser tileEntity) {
        super(world, tileEntity);
    }

    protected CraftDispenser(CraftDispenser state) {
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
    public BlockProjectileSource getBlockProjectileSource() {
        Block block = getBlock();

        if (block.getType() != Material.DISPENSER) {
            return null;
        }

        return new CraftBlockProjectileSource((BlockEntityDispenser) this.getBlockEntityFromWorld());
    }

    @Override
    public boolean dispense() {
        ensureNoWorldGeneration();
        Block block = getBlock();
        if (block.getType() == Material.DISPENSER) {
            CraftWorld world = (CraftWorld) this.getWorld();
            BlockDispenser dispense = (BlockDispenser) Blocks.DISPENSER;

            dispense.dispenseFrom(world.getHandle(), this.getHandle(), this.getPosition());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public CraftDispenser copy() {
        return new CraftDispenser(this);
    }
}
