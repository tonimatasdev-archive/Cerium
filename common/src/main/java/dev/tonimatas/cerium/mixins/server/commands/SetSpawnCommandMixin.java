package dev.tonimatas.cerium.mixins.server.commands;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.commands.SetSpawnCommand;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SetSpawnCommand.class)
public class SetSpawnCommandMixin {
    @Redirect(method = "setSpawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;setRespawnPosition(Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/core/BlockPos;FZZ)V"))
    private static void cerium$setSpawn(ServerPlayer instance, ResourceKey<Level> levelResourceKey, BlockPos arg, float arg2, boolean f, boolean bl) {
        instance.setRespawnPosition(levelResourceKey, arg, arg2, f, bl, org.bukkit.event.player.PlayerSpawnChangeEvent.Cause.COMMAND); // CraftBukkit
    }
}
