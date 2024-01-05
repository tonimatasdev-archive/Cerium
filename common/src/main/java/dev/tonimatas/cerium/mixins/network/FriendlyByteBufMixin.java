package dev.tonimatas.cerium.mixins.network;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FriendlyByteBuf.class)
public abstract class FriendlyByteBufMixin {
    @SuppressWarnings("ConstantValue")
    @WrapWithCondition(method = "writeItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z"))
    private boolean cerium$writeItem(ItemStack instance) {
        return instance.getItem() == null;
    }

    @Inject(method = "readItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;setTag(Lnet/minecraft/nbt/CompoundTag;)V", shift = At.Shift.AFTER), cancellable = true)
    private void cerium$readItem(CallbackInfoReturnable<ItemStack> cir, @Local ItemStack itemstack) {
        // CraftBukkit start
        if (itemstack.getTag() != null) {
            CraftItemStack.setItemMeta(itemstack, CraftItemStack.getItemMeta(itemstack));
        }
        // CraftBukkit end
        cir.setReturnValue(itemstack);
    }
}
