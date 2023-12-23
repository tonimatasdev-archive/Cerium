package dev.tonimatas.cerium.mixins.stats;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.network.protocol.game.ClientboundRecipePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.ServerRecipeBook;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.bukkit.craftbukkit.v1_20_R3.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ServerRecipeBook.class)
public class ServerRecipeBookMixin {
    @Redirect(method = "addRecipes", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/Recipe;isSpecial()Z"))
    public boolean cerium$addRecipes(Recipe instance, @Local RecipeHolder recipeHolder, @Local ResourceLocation resourceLocation, @Local ServerPlayer serverPlayer) {
        return !recipeHolder.value().isSpecial() && CraftEventFactory.handlePlayerRecipeListUpdateEvent(serverPlayer, resourceLocation);
    }

    @Inject(method = "sendRecipes", at = @At(value = "HEAD"), cancellable = true)
    private void cerium$sendRecipes(ClientboundRecipePacket.State state, ServerPlayer serverPlayer, List<ResourceLocation> list, CallbackInfo ci) {
        if (serverPlayer.connection == null) ci.cancel();
    }
}
