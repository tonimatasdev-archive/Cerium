package dev.tonimatas.cerium.mixins.world.item.crafting;

import dev.tonimatas.cerium.bridge.world.item.crafting.RecipeBridge;
import net.minecraft.world.item.crafting.CustomRecipe;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Recipe;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CustomRecipe.class)
public class CustomRecipeMixin implements RecipeBridge {
    // CraftBukkit start
    @Override
    public Recipe toBukkitRecipe(NamespacedKey id) {
        return new org.bukkit.craftbukkit.v1_20_R3.inventory.CraftComplexRecipe(id, (CustomRecipe) (Object) this);
    }
    // CraftBukkit end
}
