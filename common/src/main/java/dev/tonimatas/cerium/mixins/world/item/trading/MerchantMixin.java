package dev.tonimatas.cerium.mixins.world.item.trading;

import dev.tonimatas.cerium.bridge.world.item.trading.MerchantBridge;
import net.minecraft.world.item.trading.Merchant;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftMerchant;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Merchant.class)
public interface MerchantMixin extends MerchantBridge {
    @Unique
    @Override
    CraftMerchant getCraftMerchant(); // CraftBukkit
}
