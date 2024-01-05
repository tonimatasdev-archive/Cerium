package dev.tonimatas.cerium.mixins.advancements;

import dev.tonimatas.cerium.bridge.advancements.AdvancementHolderBridge;
import net.minecraft.advancements.AdvancementHolder;
import org.bukkit.craftbukkit.v1_20_R3.advancement.CraftAdvancement;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(AdvancementHolder.class)
public class AdvancementHolderMixin implements AdvancementHolderBridge {
    @Override
    public final org.bukkit.advancement.Advancement toBukkit() {
        return new CraftAdvancement((AdvancementHolder) (Object) this);
    }
}
