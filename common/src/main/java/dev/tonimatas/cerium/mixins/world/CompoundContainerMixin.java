package dev.tonimatas.cerium.mixins.world;

import dev.tonimatas.cerium.bridge.world.ContainerBridge;
import net.minecraft.world.CompoundContainer;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.InventoryHolder;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.List;

@Mixin(CompoundContainer.class)
public abstract class CompoundContainerMixin implements ContainerBridge, Container {
    @Shadow @Final private Container container1;
    @Shadow @Final private Container container2;
    @Unique private List<HumanEntity> cerium$transactions = new ArrayList<>();

    @Override
    public List<ItemStack> getContents() {
        int size = this.getContainerSize();
        List<ItemStack> ret = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            ret.add(this.getItem(i));
        }
        return ret;
    }

    @Override
    public void onOpen(CraftHumanEntity who) {
        ((ContainerBridge) this.container1).onOpen(who);
        ((ContainerBridge) this.container2).onOpen(who);
        this.cerium$transactions.add(who);
    }

    @Override
    public void onClose(CraftHumanEntity who) {
        ((ContainerBridge) this.container1).onClose(who);
        ((ContainerBridge) this.container2).onClose(who);
        this.cerium$transactions.remove(who);
    }

    @Override
    public List<HumanEntity> getViewers() {
        return cerium$transactions;
    }

    @Override
    public InventoryHolder getOwner() { return null; }

    @Override
    public int getMaxStackSize() {
        return Math.min(this.container1.getMaxStackSize(), this.container2.getMaxStackSize());
    }

    @Override
    public void setMaxStackSize(int size) {
        ((ContainerBridge) this.container1).setMaxStackSize(size);
        ((ContainerBridge) this.container2).setMaxStackSize(size);
    }

    @Override
    public Location getLocation() {
        return ((ContainerBridge) this.container1).getLocation();
    }

    @Override
    public RecipeHolder<?> getCurrentRecipe() { return null; }

    @Override
    public void setCurrentRecipe(RecipeHolder<?> recipe) { }
}