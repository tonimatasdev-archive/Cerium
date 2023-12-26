package dev.tonimatas.cerium.mixins.world.item;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.item.EndCrystalItem;
import net.minecraft.world.item.context.UseOnContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EndCrystalItem.class)
public class EndCrystalItemMixin {
    @Inject(method = "useOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z", shift = At.Shift.BEFORE), cancellable = true)
    private void cerium$useOn(UseOnContext useOnContext, CallbackInfoReturnable<InteractionResult> cir, @Local EndCrystal endCrystal) {
        if (org.bukkit.craftbukkit.v1_20_R3.event.CraftEventFactory.callEntityPlaceEvent(useOnContext, endCrystal).isCancelled()) {
            cir.setReturnValue(InteractionResult.FAIL);
        }
    }
}
