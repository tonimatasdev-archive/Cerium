package org.bukkit.craftbukkit.v1_20_R3.block;

import net.minecraft.world.level.block.entity.BlockEntityEnchantTable;
import org.bukkit.World;
import org.bukkit.block.EnchantingTable;
import org.bukkit.craftbukkit.v1_20_R3.util.CraftChatMessage;

public class CraftEnchantingTable extends CraftBlockEntityState<BlockEntityEnchantTable> implements EnchantingTable {

    public CraftEnchantingTable(World world, BlockEntityEnchantTable tileEntity) {
        super(world, tileEntity);
    }

    protected CraftEnchantingTable(CraftEnchantingTable state) {
        super(state);
    }

    @Override
    public String getCustomName() {
        BlockEntityEnchantTable enchant = this.getSnapshot();
        return enchant.hasCustomName() ? CraftChatMessage.fromComponent(enchant.getCustomName()) : null;
    }

    @Override
    public void setCustomName(String name) {
        this.getSnapshot().setCustomName(CraftChatMessage.fromStringOrNull(name));
    }

    @Override
    public void applyTo(BlockEntityEnchantTable enchantingTable) {
        super.applyTo(enchantingTable);

        if (!this.getSnapshot().hasCustomName()) {
            enchantingTable.setCustomName(null);
        }
    }

    @Override
    public CraftEnchantingTable copy() {
        return new CraftEnchantingTable(this);
    }
}
