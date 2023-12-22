package org.bukkit.craftbukkit.v1_20_R3.block;

import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.world.item.EnumColor;
import net.minecraft.world.level.block.BlockShulkerBox;
import net.minecraft.world.level.block.entity.BlockEntityShulkerBox;
import org.bukkit.DyeColor;
import org.bukkit.World;
import org.bukkit.block.ShulkerBox;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftInventory;
import org.bukkit.craftbukkit.v1_20_R3.util.CraftMagicNumbers;
import org.bukkit.inventory.Inventory;

public class CraftShulkerBox extends CraftLootable<BlockEntityShulkerBox> implements ShulkerBox {

    public CraftShulkerBox(World world, BlockEntityShulkerBox tileEntity) {
        super(world, tileEntity);
    }

    protected CraftShulkerBox(CraftShulkerBox state) {
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
    public DyeColor getColor() {
        EnumColor color = ((BlockShulkerBox) CraftMagicNumbers.getBlock(this.getType())).color;

        return (color == null) ? null : DyeColor.getByWoolData((byte) color.getId());
    }

    @Override
    public void open() {
        requirePlaced();
        if (!getBlockEntity().opened && getWorldHandle() instanceof net.minecraft.world.level.Level) {
            net.minecraft.world.level.Level world = getBlockEntity().getLevel();
            world.blockEvent(getPosition(), getBlockEntity().getBlockState().getBlock(), 1, 1);
            world.playSound(null, getPosition(), SoundEffects.SHULKER_BOX_OPEN, SoundCategory.BLOCKS, 0.5F, world.random.nextFloat() * 0.1F + 0.9F);
        }
        getBlockEntity().opened = true;
    }

    @Override
    public void close() {
        requirePlaced();
        if (getBlockEntity().opened && getWorldHandle() instanceof net.minecraft.world.level.Level) {
            net.minecraft.world.level.Level world = getBlockEntity().getLevel();
            world.blockEvent(getPosition(), getBlockEntity().getBlockState().getBlock(), 1, 0);
            world.playSound(null, getPosition(), SoundEffects.SHULKER_BOX_OPEN, SoundCategory.BLOCKS, 0.5F, world.random.nextFloat() * 0.1F + 0.9F);
        }
        getBlockEntity().opened = false;
    }

    @Override
    public CraftShulkerBox copy() {
        return new CraftShulkerBox(this);
    }
}
