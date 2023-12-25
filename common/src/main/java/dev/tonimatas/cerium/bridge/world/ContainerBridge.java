package dev.tonimatas.cerium.bridge.world;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftInventory;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.List;

public interface ContainerBridge {

    int MAX_STACK = 64;

    default List<ItemStack> getContents() {
        return List.of();
    }

    void onOpen(CraftHumanEntity who);

    void onClose(CraftHumanEntity who);

    List<HumanEntity> getViewers();

    InventoryHolder getOwner();

    void setMaxStackSize(int size);

    Location getLocation();

    default RecipeHolder<?> getCurrentRecipe() {
        return null;
    }

    default void setCurrentRecipe(RecipeHolder<?> recipe) {
    }

    default Inventory getOwnerInventory() {
        InventoryHolder owner = this.getOwner();
        if (owner != null) {
            return owner.getInventory();
        } else {
            return new CraftInventory((Container) this);
        }
    }
}
