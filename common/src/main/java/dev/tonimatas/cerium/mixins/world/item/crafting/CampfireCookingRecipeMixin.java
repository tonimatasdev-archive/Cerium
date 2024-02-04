package dev.tonimatas.cerium.mixins.world.item.crafting;

import dev.tonimatas.cerium.bridge.world.item.crafting.RecipeBridge;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftCampfireRecipe;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftRecipe;
import org.bukkit.inventory.Recipe;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CampfireCookingRecipe.class)
public class CampfireCookingRecipeMixin implements RecipeBridge {

    // CraftBukkit start
    @Override
    public Recipe toBukkitRecipe(NamespacedKey id) {
        CraftItemStack result = CraftItemStack.asCraftMirror(((CampfireCookingRecipe) (Object) this).result);

        CraftCampfireRecipe recipe = new CraftCampfireRecipe(id, result, CraftRecipe.toBukkit(((CampfireCookingRecipe) (Object) this).ingredient), ((CampfireCookingRecipe) (Object) this).experience, ((CampfireCookingRecipe) (Object) this).cookingTime);
        recipe.setGroup(((CampfireCookingRecipe) (Object) this).group);
        recipe.setCategory(CraftRecipe.getCategory(((CampfireCookingRecipe) (Object) this).category()));

        return recipe;
    }
    // CraftBukkit end
}
