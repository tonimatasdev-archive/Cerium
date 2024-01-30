package dev.tonimatas.cerium.mixins.world.inventory;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.FurnaceResultSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(FurnaceResultSlot.class)
public class FurnaceResultSlotMixin {
    @Inject(method = "checkTakeAchievements", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/AbstractFurnaceBlockEntity;awardUsedRecipesAndPopExperience(Lnet/minecraft/server/level/ServerPlayer;)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void cerium$checkTakeAchievements(ItemStack itemStack, CallbackInfo ci, ServerPlayer serverPlayer, AbstractFurnaceBlockEntity abstractFurnaceBlockEntity, Container var4) {
        abstractFurnaceBlockEntity.awardUsedRecipesAndPopExperience(entityplayer, itemstack, this.removeCount); // CraftBukkit
    }
}
