package dev.tonimatas.cerium.mixins.server;

import net.minecraft.commands.Commands;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerFunctionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerFunctionManager.class)
public class ServerFunctionManagerMixin {
    @Redirect(method = "getDispatcher", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;getCommands()Lnet/minecraft/commands/Commands;"))
    private Commands cerium$getDispatcher(MinecraftServer instance) {
        return instance.vanillaCommandDispatcher; // CraftBukkit
    }
}
