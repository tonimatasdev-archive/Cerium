package dev.tonimatas.cerium.mixins.network.protocol.game;

import dev.tonimatas.cerium.bridge.world.level.border.WorldBorderBridge;
import net.minecraft.network.protocol.game.ClientboundInitializeBorderPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.border.WorldBorder;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientboundInitializeBorderPacket.class)
public class ClientboundInitializeBorderPacketMixin {
    @Shadow @Mutable @Final private double newCenterX;
    @Shadow @Mutable @Final private double newCenterZ;

    @Inject(method = "<init>(Lnet/minecraft/world/level/border/WorldBorder;)V", at = @At("RETURN"))
    private void cerium$init(WorldBorder border, CallbackInfo ci) {
        Level level = ((WorldBorderBridge) border).bridge$getWorld();
        this.newCenterX = border.getCenterX() * (level != null ? level.dimensionType().coordinateScale() : 1.0);
        this.newCenterZ = border.getCenterZ() * (level != null ? level.dimensionType().coordinateScale() : 1.0);
    }
}
