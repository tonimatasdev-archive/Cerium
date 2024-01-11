package dev.tonimatas.cerium.util;

import joptsimple.OptionSet;
import net.minecraft.core.BlockPos;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.spongepowered.asm.mixin.injection.At;

import java.util.concurrent.atomic.AtomicReference;

public class CeriumValues {
    public static BlockPos openSign; // CraftBukkit
    public static AtomicReference<EntityPotionEffectEvent.Cause> potionEffectCause = new AtomicReference<>(EntityPotionEffectEvent.Cause.UNKNOWN); // CraftBukkit
    public static AtomicReference<OptionSet> optionSet = new AtomicReference<>(null);
}
