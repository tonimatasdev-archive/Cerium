package dev.tonimatas.cerium.mixins.server.commands;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.commands.WorldBorderCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.border.WorldBorder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WorldBorderCommand.class)
public class WorldBorderCommandMixin {
    @Redirect(method = "setDamageBuffer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;getWorldBorder()Lnet/minecraft/world/level/border/WorldBorder;"))
    private static WorldBorder cerium$setDamageBuffer(ServerLevel instance, CommandSourceStack commandSourceStack) {
        return commandSourceStack.getLevel().getWorldBorder(); // CraftBukkit
    }

    @Redirect(method = "setDamageAmount", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;getWorldBorder()Lnet/minecraft/world/level/border/WorldBorder;"))
    private static WorldBorder cerium$setDamageAmount(ServerLevel instance, CommandSourceStack commandSourceStack) {
        return commandSourceStack.getLevel().getWorldBorder(); // CraftBukkit
    }

    @Redirect(method = "setWarningTime", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;getWorldBorder()Lnet/minecraft/world/level/border/WorldBorder;"))
    private static WorldBorder cerium$setWarningTime(ServerLevel instance, CommandSourceStack commandSourceStack) {
        return commandSourceStack.getLevel().getWorldBorder(); // CraftBukkit
    }

    @Redirect(method = "setWarningDistance", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;getWorldBorder()Lnet/minecraft/world/level/border/WorldBorder;"))
    private static WorldBorder cerium$setWarningDistance(ServerLevel instance, CommandSourceStack commandSourceStack) {
        return commandSourceStack.getLevel().getWorldBorder(); // CraftBukkit
    }

    @Redirect(method = "getSize", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;getWorldBorder()Lnet/minecraft/world/level/border/WorldBorder;"))
    private static WorldBorder cerium$getSize(ServerLevel instance, CommandSourceStack commandSourceStack) {
        return commandSourceStack.getLevel().getWorldBorder(); // CraftBukkit
    }

    @Redirect(method = "setCenter", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;getWorldBorder()Lnet/minecraft/world/level/border/WorldBorder;"))
    private static WorldBorder cerium$setCenter(ServerLevel instance, CommandSourceStack commandSourceStack) {
        return commandSourceStack.getLevel().getWorldBorder(); // CraftBukkit
    }

    @Redirect(method = "setSize", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;getWorldBorder()Lnet/minecraft/world/level/border/WorldBorder;"))
    private static WorldBorder cerium$setSize(ServerLevel instance, CommandSourceStack commandSourceStack) {
        return commandSourceStack.getLevel().getWorldBorder(); // CraftBukkit
    }
}
