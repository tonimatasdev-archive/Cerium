package org.bukkit.craftbukkit.v1_20_R3.block;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.entity.BarrelBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.World;
import org.bukkit.block.Barrel;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftInventory;
import org.bukkit.inventory.Inventory;

public class CraftBarrel extends CraftLootable<BarrelBlockEntity> implements Barrel {

    public CraftBarrel(World world, BarrelBlockEntity tileEntity) {
        super(world, tileEntity);
    }

    protected CraftBarrel(CraftBarrel state) {
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
    public void open() {
        requirePlaced();
        if (!getBlockEntity().openersCounter.opened) {
            BlockState blockData = getBlockEntity().getBlockState();
            boolean open = blockData.getValue(BarrelBlock.OPEN);

            if (!open) {
                getBlockEntity().updateBlockState(blockData, true);
                if (getWorldHandle() instanceof net.minecraft.world.level.Level) {
                    getBlockEntity().playSound(blockData, SoundEvents.BARREL_OPEN);
                }
            }
        }
        getBlockEntity().openersCounter.opened = true;
    }

    @Override
    public void close() {
        requirePlaced();
        if (getBlockEntity().openersCounter.opened) {
            BlockState blockData = getBlockEntity().getBlockState();
            getBlockEntity().updateBlockState(blockData, false);
            if (getWorldHandle() instanceof net.minecraft.world.level.Level) {
                getBlockEntity().playSound(blockData, SoundEvents.BARREL_CLOSE);
            }
        }
        getBlockEntity().openersCounter.opened = false;
    }

    @Override
    public CraftBarrel copy() {
        return new CraftBarrel(this);
    }
}
