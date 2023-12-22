package org.bukkit.craftbukkit.v1_20_R3.inventory;

import net.minecraft.world.IInventory;
import org.bukkit.inventory.StonecutterInventory;

public class CraftInventoryStonecutter extends CraftResultInventory implements StonecutterInventory {

    public CraftInventoryStonecutter(IInventory inventory, IInventory resultInventory) {
        super(inventory, resultInventory);
    }
}
