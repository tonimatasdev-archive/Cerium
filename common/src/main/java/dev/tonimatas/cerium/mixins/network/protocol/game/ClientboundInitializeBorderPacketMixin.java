package dev.tonimatas.cerium.mixins.network.protocol.game;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.network.protocol.game.ClientboundInitializeBorderPacket;
import net.minecraft.world.level.border.WorldBorder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientboundInitializeBorderPacket.class)
public class ClientboundInitializeBorderPacketMixin {
    @Redirect(method = "<init>(Lnet/minecraft/world/level/border/WorldBorder;)V", at = @At(value = "FIELD", target = "Lnet/minecraft/network/protocol/game/ClientboundInitializeBorderPacket;newCenterX:D"))
    private void cerium$newCenterX(ClientboundInitializeBorderPacket instance, double value, @Local WorldBorder worldBorder) {
        return value * worldBorder.world.dimensionType().coordinateScale();
    }

    @Redirect(method = "<init>(Lnet/minecraft/world/level/border/WorldBorder;)V", at = @At(value = "FIELD", target = "Lnet/minecraft/network/protocol/game/ClientboundInitializeBorderPacket;newCenterZ:D"))
    private void cerium$newCenterZ(ClientboundInitializeBorderPacket instance, double value, @Local WorldBorder worldBorder) {
        return value * worldBorder.world.dimensionType().coordinateScale();
    }
}
