package dev.tonimatas.cerium.mixins.world.level;

import dev.tonimatas.cerium.bridge.world.ContainerBridge;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.InventoryHolder;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Collections;
import java.util.List;

@Mixin(Container.class)
public interface ContainerMixin extends ContainerBridge {
    @Override
    default void onOpen(CraftHumanEntity who) {
    }

    @Override
    default void onClose(CraftHumanEntity who) {
    }

    @Override
    default List<HumanEntity> getViewers() {
        return Collections.emptyList();
    }

    @Override
    default InventoryHolder getOwner() {
        return null;
    }

    @Override
    default void setMaxStackSize(int size) {
    }

    @Override
    default Location getLocation() {
        return null;
    }

    @Override
    default RecipeHolder<?> getCurrentRecipe() {
        return null;
    }

    @Override
    default void setCurrentRecipe(RecipeHolder<?> recipe) {
    }
}
