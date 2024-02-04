package dev.tonimatas.cerium.mixins.world.item.crafting;

import dev.tonimatas.cerium.bridge.world.item.crafting.RecipeBridge;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.bukkit.craftbukkit.v1_20_R3.util.CraftNamespacedKey;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(RecipeHolder.class)
public class RecipeHolderMixin <T extends Recipe<?>> {
    @Shadow @Final private T value;

    @Shadow @Final private ResourceLocation id;

    // CraftBukkit start
    @Unique
    public final org.bukkit.inventory.Recipe toBukkitRecipe() {
        return ((RecipeBridge) this.value).toBukkitRecipe(CraftNamespacedKey.fromMinecraft(this.id));
    }
    // CraftBukkit end
}
