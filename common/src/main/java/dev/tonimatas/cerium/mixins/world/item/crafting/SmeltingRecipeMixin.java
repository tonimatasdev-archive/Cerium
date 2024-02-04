package dev.tonimatas.cerium.mixins.world.item.crafting;

import dev.tonimatas.cerium.bridge.world.item.crafting.RecipeBridge;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftFurnaceRecipe;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftRecipe;
import org.bukkit.inventory.Recipe;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SmeltingRecipe.class)
public abstract class SmeltingRecipeMixin implements RecipeBridge {
    // CraftBukkit start
    @Override
    public Recipe toBukkitRecipe(NamespacedKey id) {
        CraftItemStack result = CraftItemStack.asCraftMirror(((SmeltingRecipe) (Object) this).result);

        CraftFurnaceRecipe recipe = new CraftFurnaceRecipe(id, result, CraftRecipe.toBukkit(((SmeltingRecipe) (Object) this).ingredient), ((SmeltingRecipe) (Object) this).experience, ((SmeltingRecipe) (Object) this).cookingTime);
        recipe.setGroup(((SmeltingRecipe) (Object) this).group);
        recipe.setCategory(CraftRecipe.getCategory(((SmeltingRecipe) (Object) this).category()));

        return recipe;
    }
    // CraftBukkit end
}
