package dev.tonimatas.cerium.mixins.world.entity.npc;

import dev.tonimatas.cerium.bridge.world.entity.EntityBridge;
import dev.tonimatas.cerium.bridge.world.item.trading.MerchantOfferBridge;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftMerchantRecipe;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WanderingTrader.class)
public abstract class WanderingTraderMixin {
    @Shadow protected abstract void experimentalUpdateTrades();

    @Inject(method = "<init>", at = @At("TAIL"))
    private void cerium$init(EntityType<?> entityType, Level level, CallbackInfo ci) {
        ((WanderingTrader) (Object) this).setDespawnDelay(48000); // CraftBukkit - set default from MobSpawnerTrader
    }
    
    /**
     * @author TonimatasDEV
     * @reason CraftBukkit
     */
    @Overwrite
    protected void updateTrades() {
        if (((WanderingTrader) (Object) this).level().enabledFeatures().contains(FeatureFlags.TRADE_REBALANCE)) {
            this.experimentalUpdateTrades();
        } else {
            VillagerTrades.ItemListing[] itemListings = (VillagerTrades.ItemListing[])VillagerTrades.WANDERING_TRADER_TRADES.get(1);
            VillagerTrades.ItemListing[] itemListings2 = (VillagerTrades.ItemListing[])VillagerTrades.WANDERING_TRADER_TRADES.get(2);
            if (itemListings != null && itemListings2 != null) {
                MerchantOffers merchantOffers = ((AbstractVillager) (Object) this).getOffers();
                ((AbstractVillager) (Object) this).addOffersFromItemListings(merchantOffers, itemListings, 5);
                int i = ((WanderingTrader) (Object) this).random.nextInt(itemListings2.length);
                VillagerTrades.ItemListing itemListing = itemListings2[i];
                MerchantOffer merchantOffer = itemListing.getOffer(((WanderingTrader) (Object) this), ((WanderingTrader) (Object) this).random);
                if (merchantOffer != null) {
                    // CraftBukkit start
                    VillagerAcquireTradeEvent event = new VillagerAcquireTradeEvent((AbstractVillager) getBukkitEntity(), ((MerchantOfferBridge) merchantOffer).asBukkit());
                    // Suppress during worldgen
                    if (((EntityBridge) this).bridge$getValid()) {
                        Bukkit.getPluginManager().callEvent(event);
                    }
                    if (!event.isCancelled()) {
                        merchantOffers.add(CraftMerchantRecipe.fromBukkit(event.getRecipe()).toMinecraft());
                    }
                    // CraftBukkit end
                }

            }
        }
    }
}
