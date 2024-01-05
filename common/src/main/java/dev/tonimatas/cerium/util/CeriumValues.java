package dev.tonimatas.cerium.util;

import net.minecraft.core.BlockPos;
import org.bukkit.event.entity.EntityPotionEffectEvent;

import java.util.concurrent.atomic.AtomicReference;

public class CeriumValues {
    public static BlockPos openSign; // CraftBukkit

    public static AtomicReference<EntityPotionEffectEvent.Cause> potionEffectCause = new AtomicReference<>(EntityPotionEffectEvent.Cause.UNKNOWN); // CraftBukkit
}
