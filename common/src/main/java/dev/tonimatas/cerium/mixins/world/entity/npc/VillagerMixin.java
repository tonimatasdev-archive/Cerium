package dev.tonimatas.cerium.mixins.world.entity.npc;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import dev.tonimatas.cerium.bridge.world.entity.LivingEntityBridge;
import dev.tonimatas.cerium.bridge.world.item.trading.MerchantOfferBridge;
import dev.tonimatas.cerium.util.CeriumValues;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.trading.MerchantOffer;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_20_R3.event.CraftEventFactory;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.EntityTransformEvent;
import org.bukkit.event.entity.VillagerReplenishTradeEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Villager.class)
public class VillagerMixin {
    @Inject(method = "customServerAiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/npc/Villager;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;)Z"))
    private void cerium$customServerAiStep(CallbackInfo ci) {
        ((LivingEntityBridge) this).cerium$addEffectCause(EntityPotionEffectEvent.Cause.VILLAGER_TRADE);
    }
    
    @WrapWithCondition(method = "restock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/trading/MerchantOffer;resetUses()V"))
    private boolean cerium$restock(MerchantOffer instance) {
        // CraftBukkit start
        VillagerReplenishTradeEvent event = new VillagerReplenishTradeEvent((Villager) this.getBukkitEntity(), ((MerchantOfferBridge) instance).asBukkit());
        Bukkit.getPluginManager().callEvent(event);
        return event.isCancelled();
        // CraftBukkit end
    }

    @WrapWithCondition(method = "catchUpDemand", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/trading/MerchantOffer;resetUses()V"))
    private boolean cerium$catchUpDemand(MerchantOffer instance) {
        // CraftBukkit start
        VillagerReplenishTradeEvent event = new VillagerReplenishTradeEvent((Villager) this.getBukkitEntity(), ((MerchantOfferBridge) instance).asBukkit());
        Bukkit.getPluginManager().callEvent(event);
        return event.isCancelled();
        // CraftBukkit end
    }

    @Inject(method = "thunderHit", cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;addFreshEntityWithPassengers(Lnet/minecraft/world/entity/Entity;)V"))
    private void cerium$thunderHit(ServerLevel serverLevel, LightningBolt lightningBolt, CallbackInfo ci, Witch witch) {
        if (CraftEventFactory.callEntityTransformEvent((net.minecraft.world.entity.npc.Villager) (Object) this, witch, EntityTransformEvent.TransformReason.LIGHTNING).isCancelled()) {
            ci.cancel();
        } else {
            // TODO
            serverLevel.cerium$addCause(CreatureSpawnEvent.SpawnReason.LIGHTNING);
        }
    }
    
    @Inject(method = "spawnGolemIfNeeded", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/SpawnUtil;trySpawnMob(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/entity/MobSpawnType;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;IIILnet/minecraft/util/SpawnUtil$Strategy;)Ljava/util/Optional;"))
    private void cerium$spawnGolemIfNeeded(ServerLevel serverLevel, long l, int i, CallbackInfo ci) {
        CeriumValues.spawnReason$trySpawnMob.set(CreatureSpawnEvent.SpawnReason.VILLAGE_DEFENSE);
    }
}
