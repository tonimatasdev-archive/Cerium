package dev.tonimatas.cerium.mixins.server.commands;

import net.minecraft.server.commands.SpreadPlayersCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.RelativeMovement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Set;

@Mixin(SpreadPlayersCommand.class)
public class SpreadPlayersCommandMixin {
    @Redirect(method = "setPlayerPositions", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;teleportTo(Lnet/minecraft/server/level/ServerLevel;DDDLjava/util/Set;FF)Z"))
    private static boolean cerium$setPlayerPositions(Entity instance, ServerLevel serverLevel, double f, double arg, double d, Set<RelativeMovement> e, float g, float set) {
        return instance.teleportTo(serverLevel, f, arg, d, e, g, set, org.bukkit.event.player.PlayerTeleportEvent.TeleportCause.COMMAND); // CraftBukkit - handle teleport reason
    }
}
