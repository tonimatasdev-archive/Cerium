package dev.tonimatas.cerium.mixins.world.entity.npc;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.item.ItemStack;
import org.bukkit.craftbukkit.v1_20_R3.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(InventoryCarrier.class)
public class InventoryCarrierMixin {
    @Inject(method = "pickUpItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;onItemPickup(Lnet/minecraft/world/entity/item/ItemEntity;)V"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private static void cerium$pickUpItem(Mob mob, InventoryCarrier inventoryCarrier, ItemEntity itemEntity, CallbackInfo ci, ItemStack itemStack, SimpleContainer simpleContainer) {
        // CraftBukkit start
        ItemStack remaining = new SimpleContainer(simpleContainer).addItem(itemStack);
        if (CraftEventFactory.callEntityPickupItemEvent(mob, itemEntity, remaining.getCount(), false).isCancelled()) {
            ci.cancel();
        }
        // CraftBukkit end
    }
}
