package dev.tonimatas.cerium.mixins.network.protocol.common;

import com.llamalad7.mixinextras.sugar.Local;
import dev.tonimatas.cerium.util.CeriumClasses;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerboundCustomPayloadPacket.class)
public class ServerboundCustomPayloadPacketMixin {
    @Inject(method = "readUnknownPayload", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/FriendlyByteBuf;skipBytes(I)Lnet/minecraft/network/FriendlyByteBuf;", shift = At.Shift.BEFORE), cancellable = true)
    private static void cerium$readUnknownPayload(ResourceLocation resourceLocation, FriendlyByteBuf friendlyByteBuf, CallbackInfoReturnable<CeriumClasses.UnknownPayload> cir, @Local int i) {
        cir.setReturnValue(new CeriumClasses.UnknownPayload(resourceLocation, friendlyByteBuf.readBytes(i)));
    }
}
