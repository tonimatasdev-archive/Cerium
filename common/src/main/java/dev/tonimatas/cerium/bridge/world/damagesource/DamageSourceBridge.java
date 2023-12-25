package dev.tonimatas.cerium.bridge.world.damagesource;

import net.minecraft.world.damagesource.DamageSource;

public interface DamageSourceBridge {
    boolean bridge$isSweep();

    DamageSource bridge$sweep();

    boolean bridge$isMelting();

    DamageSource bridge$melting();

    boolean bridge$isPoison();

    DamageSource bridge$poison();
}
