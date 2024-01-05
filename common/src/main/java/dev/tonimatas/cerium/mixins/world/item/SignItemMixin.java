package dev.tonimatas.cerium.mixins.world.item;

import dev.tonimatas.cerium.util.CeriumValues;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.SignItem;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SignItem.class)
public class SignItemMixin {
    @Redirect(method = "updateCustomBlockEntityTag", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/SignBlock;openTextEdit(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/block/entity/SignBlockEntity;Z)V"))
    private void cerium$updateCustomBlockEntityTag(SignBlock instance, Player player, SignBlockEntity signBlockEntity, boolean bl, BlockPos blockPos) {
        // CraftBukkit start - SPIGOT-4678
        CeriumValues.openSign = blockPos;
        // CraftBukkit end
    }
}
