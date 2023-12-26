package org.bukkit.craftbukkit.v1_20_R3.inventory.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.*;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftInventory;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftInventoryBrewer;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftInventoryFurnace;
import org.bukkit.craftbukkit.v1_20_R3.util.CraftChatMessage;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public abstract class CraftTileInventoryConverter implements CraftInventoryCreator.InventoryConverter {

    public abstract Container getBlockEntity();

    @Override
    public Inventory createInventory(InventoryHolder holder, InventoryType type) {
        return getInventory(getBlockEntity());
    }

    @Override
    public Inventory createInventory(InventoryHolder holder, InventoryType type, String title) {
        Container te = getBlockEntity();
        if (te instanceof RandomizableContainerBlockEntity) {
            ((RandomizableContainerBlockEntity) te).setCustomName(CraftChatMessage.fromStringOrNull(title));
        }

        return getInventory(te);
    }

    public Inventory getInventory(Container tileEntity) {
        return new CraftInventory(tileEntity);
    }

    public static class Furnace extends CraftTileInventoryConverter {

        @Override
        public Container getBlockEntity() {
            AbstractFurnaceBlockEntity furnace = new FurnaceBlockEntity(BlockPos.ZERO, Blocks.FURNACE.defaultBlockState()); // TODO: customize this if required
            return furnace;
        }

        @Override
        public Inventory createInventory(InventoryHolder owner, InventoryType type, String title) {
            Container tileEntity = getBlockEntity();
            ((AbstractFurnaceBlockEntity) tileEntity).setCustomName(CraftChatMessage.fromStringOrNull(title));
            return getInventory(tileEntity);
        }

        @Override
        public Inventory getInventory(Container tileEntity) {
            return new CraftInventoryFurnace((AbstractFurnaceBlockEntity) tileEntity);
        }
    }

    public static class BrewingStand extends CraftTileInventoryConverter {

        @Override
        public Container getBlockEntity() {
            return new BrewingStandBlockEntity(BlockPos.ZERO, Blocks.BREWING_STAND.defaultBlockState());
        }

        @Override
        public Inventory createInventory(InventoryHolder holder, InventoryType type, String title) {
            // BrewingStand does not extend BlockEntityLootable
            Container tileEntity = getBlockEntity();
            if (tileEntity instanceof BrewingStandBlockEntity) {
                ((BrewingStandBlockEntity) tileEntity).setCustomName(CraftChatMessage.fromStringOrNull(title));
            }
            return getInventory(tileEntity);
        }

        @Override
        public Inventory getInventory(Container tileEntity) {
            return new CraftInventoryBrewer(tileEntity);
        }
    }

    public static class Dispenser extends CraftTileInventoryConverter {

        @Override
        public Container getBlockEntity() {
            return new DispenserBlockEntity(BlockPos.ZERO, Blocks.DISPENSER.defaultBlockState());
        }
    }

    public static class Dropper extends CraftTileInventoryConverter {

        @Override
        public Container getBlockEntity() {
            return new DropperBlockEntity(BlockPos.ZERO, Blocks.DROPPER.defaultBlockState());
        }
    }

    public static class Hopper extends CraftTileInventoryConverter {

        @Override
        public Container getBlockEntity() {
            return new HopperBlockEntity(BlockPos.ZERO, Blocks.HOPPER.defaultBlockState());
        }
    }

    public static class BlastFurnace extends CraftTileInventoryConverter {

        @Override
        public Container getBlockEntity() {
            return new BlastFurnaceBlockEntity(BlockPos.ZERO, Blocks.BLAST_FURNACE.defaultBlockState());
        }
    }

    public static class Lectern extends CraftTileInventoryConverter {

        @Override
        public Container getBlockEntity() {
            return new LecternBlockEntity(BlockPos.ZERO, Blocks.LECTERN.defaultBlockState()).bookAccess;
        }
    }

    public static class Smoker extends CraftTileInventoryConverter {

        @Override
        public Container getBlockEntity() {
            return new SmokerBlockEntity(BlockPos.ZERO, Blocks.SMOKER.defaultBlockState());
        }
    }

    public static class Crafter extends CraftTileInventoryConverter {

        @Override
        public Container getBlockEntity() {
            return new CrafterBlockEntity(BlockPos.ZERO, Blocks.CRAFTER.defaultBlockState());
        }
    }
}
