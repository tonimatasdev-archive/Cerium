package org.bukkit.craftbukkit.v1_20_R3.inventory;

import net.minecraft.world.IInventory;
import org.bukkit.inventory.LoomInventory;

public class CraftInventoryLoom extends CraftResultInventory implements LoomInventory {

    public CraftInventoryLoom(IInventory inventory, IInventory resultInventory) {
        super(inventory, resultInventory);
    }
}