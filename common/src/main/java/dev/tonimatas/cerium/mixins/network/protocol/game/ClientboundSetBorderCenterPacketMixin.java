package dev.tonimatas.cerium.mixins.network.protocol.game;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.network.protocol.game.ClientboundSetBorderCenterPacket;
import net.minecraft.world.level.border.WorldBorder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientboundSetBorderCenterPacket.class)
public class ClientboundSetBorderCenterPacketMixin {
    @Redirect(method = "<init>(Lnet/minecraft/world/level/border/WorldBorder;)V", at = @At(value = "FIELD", target = "Lnet/minecraft/network/protocol/game/ClientboundSetBorderCenterPacket;newCenterX:D"))
    private void cerium$newCenterX(ClientboundSetBorderCenterPacket instance, double value, @Local WorldBorder worldBorder) {
        return value * (worldBorder.world != null ? worldBorder.world.dimensionType().coordinateScale() : 1.0);
    }

    @Redirect(method = "<init>(Lnet/minecraft/world/level/border/WorldBorder;)V", at = @At(value = "FIELD", target = "Lnet/minecraft/network/protocol/game/ClientboundSetBorderCenterPacket;newCenterX:D"))
    private void cerium$newCenterZ(ClientboundSetBorderCenterPacket instance, double value, @Local WorldBorder worldBorder) {
        return value * (worldBorder.world != null ? worldBorder.world.dimensionType().coordinateScale() : 1.0);
    }
}
