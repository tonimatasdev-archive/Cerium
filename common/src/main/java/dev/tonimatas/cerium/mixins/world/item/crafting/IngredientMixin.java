package dev.tonimatas.cerium.mixins.world.item.crafting;

import dev.tonimatas.cerium.bridge.world.item.crafting.IngredientBridge;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Ingredient.class)
public abstract class IngredientMixin implements IngredientBridge {
    @Shadow public abstract boolean isEmpty();
    @Shadow public abstract ItemStack[] getItems();

    @Unique public boolean exact; // CraftBukkit

    @Override
    public boolean cerium$getExact() {
        return this.exact;
    }

    @Override
    public void cerium$setExact(boolean value) {
        this.exact = value;
    }

    /**
     * @author TonimatasDEV
     * @reason CraftBukkit
     */
    @Overwrite
    public boolean test(@Nullable ItemStack itemStack) {
        if (itemStack == null) {
            return false;
        } else if (this.isEmpty()) {
            return itemStack.isEmpty();
        } else {
            ItemStack[] var2 = this.getItems();
            int var3 = var2.length;
            
            for(int var4 = 0; var4 < var3; ++var4) {
                ItemStack itemStack2 = var2[var4];
                // CraftBukkit start
                if (exact) {
                    if (itemStack2.getItem() == itemStack.getItem() && ItemStack.isSameItemSameTags(itemStack, itemStack2)) {
                        return true;
                    }

                    continue;
                }
                // CraftBukkit end
                if (itemStack2.is(itemStack.getItem())) {
                    return true;
                }
            }

            return false;
        }
    }
}
