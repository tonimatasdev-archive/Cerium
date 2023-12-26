package dev.tonimatas.cerium.mixins.world.item;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.ArmorStandItem;
import net.minecraft.world.item.context.UseOnContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ArmorStandItem.class)
public class ArmorStandItemMixin {
    @Inject(method = "useOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;floor(F)I", shift = At.Shift.AFTER), cancellable = true)
    public void cerium$useOn(UseOnContext useOnContext, CallbackInfoReturnable<InteractionResult> cir, @Local ArmorStand armorStand) {
        if (org.bukkit.craftbukkit.v1_20_R3.event.CraftEventFactory.callEntityPlaceEvent(useOnContext, armorStand).isCancelled()) {
            cir.setReturnValue(InteractionResult.FAIL);
        }
    }
}
