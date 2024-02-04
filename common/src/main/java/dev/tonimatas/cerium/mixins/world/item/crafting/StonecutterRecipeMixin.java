package dev.tonimatas.cerium.mixins.world.item.crafting;

import dev.tonimatas.cerium.bridge.world.item.crafting.RecipeBridge;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftRecipe;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftStonecuttingRecipe;
import org.bukkit.inventory.Recipe;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(StonecutterRecipe.class)
public class StonecutterRecipeMixin implements RecipeBridge {
    // CraftBukkit start
    @Override
    public Recipe toBukkitRecipe(NamespacedKey id) {
        CraftItemStack result = CraftItemStack.asCraftMirror(((StonecutterRecipe) (Object) this).result);

        CraftStonecuttingRecipe recipe = new CraftStonecuttingRecipe(id, result, CraftRecipe.toBukkit(((StonecutterRecipe) (Object) this).ingredient));
        recipe.setGroup(((StonecutterRecipe) (Object) this).group);

        return recipe;
    }
    // CraftBukkit end
}
