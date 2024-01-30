package dev.tonimatas.cerium.mixins.world.item.trading;

import dev.tonimatas.cerium.bridge.world.item.trading.MerchantOfferBridge;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftMerchantRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(MerchantOffer.class)
public class MerchantOfferMixin implements MerchantOfferBridge {
    // CraftBukkit start
    @Unique private CraftMerchantRecipe bukkitHandle;

    @Unique
    @Override
    public CraftMerchantRecipe asBukkit() {
        return (bukkitHandle == null) ? bukkitHandle = new CraftMerchantRecipe((MerchantOffer) (Object) this) : bukkitHandle;
    }

    @Unique
    public MerchantOffer cerium$constructor(ItemStack itemstack, ItemStack itemstack1, ItemStack itemstack2, int uses, int maxUses, int experience, float priceMultiplier, CraftMerchantRecipe bukkit) {
        return cerium$constructor(itemstack, itemstack1, itemstack2, uses, maxUses, experience, priceMultiplier, 0, bukkit);
    }

    @Unique
    public MerchantOffer cerium$constructor(ItemStack itemstack, ItemStack itemstack1, ItemStack itemstack2, int uses, int maxUses, int experience, float priceMultiplier, int demand, CraftMerchantRecipe bukkit) {
        this.bukkitHandle = bukkit;
        return new MerchantOffer(itemstack, itemstack1, itemstack2, uses, maxUses, experience, priceMultiplier, demand);
    }
    // CraftBukkit end
    
    @Inject(method = "getCostA", at = @At(value = "INVOKE", target = "Ljava/lang/Math;max(II)I"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void cerium$getCostA(CallbackInfoReturnable<ItemStack> cir, int i) {
        if (i <= 0) cir.setReturnValue(ItemStack.EMPTY); // CraftBukkit - SPIGOT-5476
    }
    
    @Redirect(method = "take", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;shrink(I)V"))
    private void cerium$take(ItemStack instance, int i) {
        // CraftBukkit start
        if (!((MerchantOffer) (Object) this).getCostA().isEmpty()) {
            instance.shrink(((MerchantOffer) (Object) this).getCostA().getCount());
        }
        // CraftBukkit end
    }
}
