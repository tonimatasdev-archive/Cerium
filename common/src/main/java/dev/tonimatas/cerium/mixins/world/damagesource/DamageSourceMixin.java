package dev.tonimatas.cerium.mixins.world.damagesource;

import dev.tonimatas.cerium.bridge.world.damagesource.DamageSourceBridge;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(DamageSource.class)
public class DamageSourceMixin implements DamageSourceBridge {
    @Unique private boolean cerium$sweep;
    @Unique private boolean cerium$melting;
    @Unique private boolean cerium$poison;


    @Override
    public boolean bridge$isSweep() {
        return cerium$sweep;
    }

    @Override
    public DamageSource bridge$sweep() {
        this.cerium$sweep = true;
        return (DamageSource) (Object) this;
    }

    @Override
    public boolean bridge$isMelting() {
        return cerium$melting;
    }

    @Override
    public DamageSource bridge$melting() {
        this.cerium$melting = true;
        return (DamageSource) (Object) this;
    }

    @Override
    public boolean bridge$isPoison() {
        return cerium$poison;
    }

    @Override
    public DamageSource bridge$poison() {
        this.cerium$poison = true;
        return (DamageSource) (Object) this;
    }
}
