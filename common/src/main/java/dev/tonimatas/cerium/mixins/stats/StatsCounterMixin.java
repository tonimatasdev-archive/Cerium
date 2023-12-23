package dev.tonimatas.cerium.mixins.stats;

import net.minecraft.stats.Stat;
import net.minecraft.stats.StatsCounter;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StatsCounter.class)
public abstract class StatsCounterMixin {
    @Shadow public abstract int getValue(Stat<?> stat);

    @Inject(method = "increment", at = @At(value = "INVOKE", target = "Lnet/minecraft/stats/StatsCounter;getValue(Lnet/minecraft/stats/Stat;)I", shift = At.Shift.AFTER), cancellable = true)
    private void cerium$increment(Player player, Stat<?> stat, int i, CallbackInfo ci) {
        org.bukkit.event.Cancellable cancellable = org.bukkit.craftbukkit.v1_20_R3.event.CraftEventFactory.handleStatisticsIncrease(player, stat, this.getValue(stat), i);
        if (cancellable != null && cancellable.isCancelled()) {
            ci.cancel();
        }
    }
}
