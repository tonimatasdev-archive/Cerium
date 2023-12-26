package dev.tonimatas.cerium.mixins.world.level.block;

import dev.tonimatas.cerium.bridge.world.level.block.SculkSpreaderBridge;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SculkSpreader;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_20_R3.block.CraftBlock;
import org.bukkit.event.block.SculkBloomEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SculkSpreader.class)
public abstract class SculkSpreaderMixin implements SculkSpreaderBridge {
    @Shadow public abstract boolean isWorldGeneration();

    @Unique public Level cerium$level;

    @Override
    public void bridge$setLevel(Level cerium$level) {
        this.cerium$level = cerium$level;
    }

    @Inject(method = "addCursor", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", shift = At.Shift.BEFORE), cancellable = true)
    private void cerium$addCursor(SculkSpreader.ChargeCursor chargeCursor, CallbackInfo ci) {
        if (!isWorldGeneration()) { // CraftBukkit - SPIGOT-7475: Don't call event during world generation
            CraftBlock bukkitBlock = CraftBlock.at(cerium$level, chargeCursor.pos);
            SculkBloomEvent event = new SculkBloomEvent(bukkitBlock, chargeCursor.getCharge());
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                ci.cancel();
            }

            chargeCursor.charge = event.getCharge();
        }
    }
}
