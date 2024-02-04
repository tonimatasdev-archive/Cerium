package dev.tonimatas.cerium.mixins.world.item.crafting;

import dev.tonimatas.cerium.bridge.world.item.crafting.RecipeBridge;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftRecipe;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftShapedRecipe;
import org.bukkit.inventory.RecipeChoice;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ShapedRecipe.class)
public abstract class ShapedRecipeMixin implements RecipeBridge {
    @Shadow @Final private ShapedRecipePattern pattern;

    @Shadow @Final private String group;

    @Shadow public abstract CraftingBookCategory category();

    @Shadow @Final private ItemStack result;

    // CraftBukkit start
    @Override
    public org.bukkit.inventory.ShapedRecipe toBukkitRecipe(NamespacedKey id) {
        CraftItemStack result = CraftItemStack.asCraftMirror(this.result);
        CraftShapedRecipe recipe = new CraftShapedRecipe(id, result, (ShapedRecipe) (Object) this);
        recipe.setGroup(this.group);
        recipe.setCategory(CraftRecipe.getCategory(this.category()));

        switch (this.pattern.height()) {
            case 1:
                switch (this.pattern.width()) {
                    case 1:
                        recipe.shape("a");
                        break;
                    case 2:
                        recipe.shape("ab");
                        break;
                    case 3:
                        recipe.shape("abc");
                        break;
                }
                break;
            case 2:
                switch (this.pattern.width()) {
                    case 1:
                        recipe.shape("a","b");
                        break;
                    case 2:
                        recipe.shape("ab","cd");
                        break;
                    case 3:
                        recipe.shape("abc","def");
                        break;
                }
                break;
            case 3:
                switch (this.pattern.width()) {
                    case 1:
                        recipe.shape("a","b","c");
                        break;
                    case 2:
                        recipe.shape("ab","cd","ef");
                        break;
                    case 3:
                        recipe.shape("abc","def","ghi");
                        break;
                }
                break;
        }
        char c = 'a';
        for (Ingredient list : this.pattern.ingredients()) {
            RecipeChoice choice = CraftRecipe.toBukkit(list);
            if (choice != null) {
                recipe.setIngredient(c, choice);
            }

            c++;
        }
        return recipe;
    }
    // CraftBukkit end
}
