package org.bukkit.craftbukkit.v1_20_R3.inventory.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.IInventory;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.CrafterBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityBlastFurnace;
import net.minecraft.world.level.block.entity.BlockEntityBrewingStand;
import net.minecraft.world.level.block.entity.BlockEntityDispenser;
import net.minecraft.world.level.block.entity.BlockEntityDropper;
import net.minecraft.world.level.block.entity.BlockEntityFurnace;
import net.minecraft.world.level.block.entity.BlockEntityFurnaceFurnace;
import net.minecraft.world.level.block.entity.BlockEntityHopper;
import net.minecraft.world.level.block.entity.BlockEntityLectern;
import net.minecraft.world.level.block.entity.BlockEntityLootable;
import net.minecraft.world.level.block.entity.BlockEntitySmoker;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftInventory;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftInventoryBrewer;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftInventoryFurnace;
import org.bukkit.craftbukkit.v1_20_R3.util.CraftChatMessage;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public abstract class CraftTileInventoryConverter implements CraftInventoryCreator.InventoryConverter {

    public abstract IInventory getBlockEntity();

    @Override
    public Inventory createInventory(InventoryHolder holder, InventoryType type) {
        return getInventory(getBlockEntity());
    }

    @Override
    public Inventory createInventory(InventoryHolder holder, InventoryType type, String title) {
        IInventory te = getBlockEntity();
        if (te instanceof BlockEntityLootable) {
            ((BlockEntityLootable) te).setCustomName(CraftChatMessage.fromStringOrNull(title));
        }

        return getInventory(te);
    }

    public Inventory getInventory(IInventory tileEntity) {
        return new CraftInventory(tileEntity);
    }

    public static class Furnace extends CraftTileInventoryConverter {

        @Override
        public IInventory getBlockEntity() {
            BlockEntityFurnace furnace = new BlockEntityFurnaceFurnace(BlockPos.ZERO, Blocks.FURNACE.defaultBlockState()); // TODO: customize this if required
            return furnace;
        }

        @Override
        public Inventory createInventory(InventoryHolder owner, InventoryType type, String title) {
            IInventory tileEntity = getBlockEntity();
            ((BlockEntityFurnace) tileEntity).setCustomName(CraftChatMessage.fromStringOrNull(title));
            return getInventory(tileEntity);
        }

        @Override
        public Inventory getInventory(IInventory tileEntity) {
            return new CraftInventoryFurnace((BlockEntityFurnace) tileEntity);
        }
    }

    public static class BrewingStand extends CraftTileInventoryConverter {

        @Override
        public IInventory getBlockEntity() {
            return new BlockEntityBrewingStand(BlockPos.ZERO, Blocks.BREWING_STAND.defaultBlockState());
        }

        @Override
        public Inventory createInventory(InventoryHolder holder, InventoryType type, String title) {
            // BrewingStand does not extend BlockEntityLootable
            IInventory tileEntity = getBlockEntity();
            if (tileEntity instanceof BlockEntityBrewingStand) {
                ((BlockEntityBrewingStand) tileEntity).setCustomName(CraftChatMessage.fromStringOrNull(title));
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
        public IInventory getBlockEntity() {
            return new BlockEntityDispenser(BlockPos.ZERO, Blocks.DISPENSER.defaultBlockState());
        }
    }

    public static class Dropper extends CraftTileInventoryConverter {

        @Override
        public IInventory getBlockEntity() {
            return new BlockEntityDropper(BlockPos.ZERO, Blocks.DROPPER.defaultBlockState());
        }
    }

    public static class Hopper extends CraftTileInventoryConverter {

        @Override
        public IInventory getBlockEntity() {
            return new BlockEntityHopper(BlockPos.ZERO, Blocks.HOPPER.defaultBlockState());
        }
    }

    public static class BlastFurnace extends CraftTileInventoryConverter {

        @Override
        public IInventory getBlockEntity() {
            return new BlockEntityBlastFurnace(BlockPos.ZERO, Blocks.BLAST_FURNACE.defaultBlockState());
        }
    }

    public static class Lectern extends CraftTileInventoryConverter {

        @Override
        public IInventory getBlockEntity() {
            return new BlockEntityLectern(BlockPos.ZERO, Blocks.LECTERN.defaultBlockState()).bookAccess;
        }
    }

    public static class Smoker extends CraftTileInventoryConverter {

        @Override
        public IInventory getBlockEntity() {
            return new BlockEntitySmoker(BlockPos.ZERO, Blocks.SMOKER.defaultBlockState());
        }
    }

    public static class Crafter extends CraftTileInventoryConverter {

        @Override
        public IInventory getBlockEntity() {
            return new CrafterBlockEntity(BlockPos.ZERO, Blocks.CRAFTER.defaultBlockState());
        }
    }
}
