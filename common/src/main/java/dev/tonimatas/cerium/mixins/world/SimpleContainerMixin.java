package dev.tonimatas.cerium.mixins.world;

import dev.tonimatas.cerium.bridge.world.ContainerBridge;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;
import org.spongepowered.asm.mixin.Mixin;

import java.util.List;

@Mixin(SimpleContainer.class)
public class SimpleContainerMixin implements ContainerBridge {

    // CraftBukkit start - add fields and methods
    public List<HumanEntity> transaction = new java.util.ArrayList<HumanEntity>();
    private int maxStack = MAX_STACK;
    protected org.bukkit.inventory.InventoryHolder bukkitOwner;

    public List<ItemStack> getContents() {
        return this.items;
    }

    public void onOpen(CraftHumanEntity who) {
        transaction.add(who);
    }

    public void onClose(CraftHumanEntity who) {
        transaction.remove(who);
    }

    public List<HumanEntity> getViewers() {
        return transaction;
    }

    @Override
    public int getMaxStackSize() {
        return maxStack;
    }

    public void setMaxStackSize(int i) {
        maxStack = i;
    }

    public org.bukkit.inventory.InventoryHolder getOwner() {
        return bukkitOwner;
    }

    @Override
    public Location getLocation() {
        return null;
    }
     /* TODO: Finish this
    public InventorySubcontainer(InventorySubcontainer original) {
        this(original.size);
        for (int slot = 0; slot < original.size; slot++) {
            this.items.set(slot, original.items.get(slot).copy());
        }
    }

    public InventorySubcontainer(int i) {
        this(i, null);
    }
    
    public InventorySubcontainer(int i, org.bukkit.inventory.InventoryHolder owner) {
         this.bukkitOwner = owner;
         // CraftBukkit end
         this.size = i;
         this.items = NonNullList.withSize(i, ItemStack.EMPTY);
     }
    */
}
