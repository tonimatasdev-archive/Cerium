package dev.tonimatas.cerium.mixins.world.item;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.item.context.UseOnContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RecordItem.class)
public class RecordItemMixin {
    @Inject(method = "useOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/context/UseOnContext;getPlayer()Lnet/minecraft/world/entity/player/Player;", shift = At.Shift.BEFORE), cancellable = true)
    private void cerium$useOn(UseOnContext useOnContext, CallbackInfoReturnable<InteractionResult> cir) {
        cir.setReturnValue(InteractionResult.SUCCESS);
    }
}
