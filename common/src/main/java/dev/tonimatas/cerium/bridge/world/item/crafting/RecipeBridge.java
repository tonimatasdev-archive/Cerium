package dev.tonimatas.cerium.bridge.world.item.crafting;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Recipe;

public interface RecipeBridge {
    public Recipe toBukkitRecipe(NamespacedKey id);
}
