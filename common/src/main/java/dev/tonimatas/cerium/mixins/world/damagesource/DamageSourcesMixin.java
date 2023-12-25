package dev.tonimatas.cerium.mixins.world.damagesource;

import dev.tonimatas.cerium.bridge.world.damagesource.DamageSourceBridge;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DamageSources.class)
public abstract class DamageSourcesMixin {
    @Shadow protected abstract DamageSource source(ResourceKey<DamageType> resourceKey);

    @Unique public DamageSource cerium$melting;
    @Unique public DamageSource cerium$poison;

    @Inject(method = "<init>", at = @At(value = "RETURN"))
    private void cerium(RegistryAccess registryAccess, CallbackInfo ci) {
        this.cerium$melting = ((DamageSourceBridge) this.source(DamageTypes.ON_FIRE)).bridge$melting();
        this.cerium$poison = ((DamageSourceBridge) this.source(DamageTypes.MAGIC)).bridge$poison();

    }
}
