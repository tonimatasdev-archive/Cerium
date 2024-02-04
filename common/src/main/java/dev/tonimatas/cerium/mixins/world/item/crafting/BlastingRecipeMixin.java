package dev.tonimatas.cerium.mixins.world.item.crafting;

import dev.tonimatas.cerium.bridge.world.item.crafting.RecipeBridge;
import net.minecraft.world.item.crafting.BlastingRecipe;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftBlastingRecipe;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftRecipe;
import org.bukkit.inventory.Recipe;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlastingRecipe.class)
public class BlastingRecipeMixin implements RecipeBridge {
    // CraftBukkit start
    @Override
    public Recipe toBukkitRecipe(NamespacedKey id) {
        CraftItemStack result = CraftItemStack.asCraftMirror(((BlastingRecipe) (Object) this).result);

        CraftBlastingRecipe recipe = new CraftBlastingRecipe(id, result, CraftRecipe.toBukkit(((BlastingRecipe) (Object) this).ingredient), ((BlastingRecipe) (Object) this).experience, ((BlastingRecipe) (Object) this).cookingTime);
        recipe.setGroup(((BlastingRecipe) (Object) this).group);
        recipe.setCategory(CraftRecipe.getCategory(((BlastingRecipe) (Object) this).category()));

        return recipe;
    }
    // CraftBukkit end
}
