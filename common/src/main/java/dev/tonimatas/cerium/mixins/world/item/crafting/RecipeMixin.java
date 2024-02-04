package dev.tonimatas.cerium.mixins.world.item.crafting;

import net.minecraft.world.item.crafting.Recipe;
import org.bukkit.NamespacedKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Recipe.class)
public interface RecipeMixin {
    // CraftBukkit start
    @Unique
    org.bukkit.inventory.Recipe toBukkitRecipe(NamespacedKey id); // CraftBukkit
    // CraftBukkit end
}
