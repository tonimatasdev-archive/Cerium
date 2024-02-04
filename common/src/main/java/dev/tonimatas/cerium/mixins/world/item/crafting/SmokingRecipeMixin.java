package dev.tonimatas.cerium.mixins.world.item.crafting;

import dev.tonimatas.cerium.bridge.world.item.crafting.RecipeBridge;
import net.minecraft.world.item.crafting.SmokingRecipe;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftRecipe;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftSmokingRecipe;
import org.bukkit.inventory.Recipe;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SmokingRecipe.class)
public class SmokingRecipeMixin implements RecipeBridge {
    // CraftBukkit start
    @Override
    public Recipe toBukkitRecipe(NamespacedKey id) {
        CraftItemStack result = CraftItemStack.asCraftMirror(((SmokingRecipe) (Object) this).result);

        CraftSmokingRecipe recipe = new CraftSmokingRecipe(id, result, CraftRecipe.toBukkit(((SmokingRecipe) (Object) this).ingredient), ((SmokingRecipe) (Object) this).experience, ((SmokingRecipe) (Object) this).cookingTime);
        recipe.setGroup(((SmokingRecipe) (Object) this).group);
        recipe.setCategory(CraftRecipe.getCategory(((SmokingRecipe) (Object) this).category()));

        return recipe;
    }
    // CraftBukkit end
}
