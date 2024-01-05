package dev.tonimatas.cerium.mixins.server.commands;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.commands.ScheduleCommand;
import net.minecraft.world.level.storage.WorldData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ScheduleCommand.class)
public class ScheduleCommandMixin {
    @Redirect(method = "schedule", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;getWorldData()Lnet/minecraft/world/level/storage/WorldData;"))
    private static WorldData cerium$schedule(MinecraftServer instance, CommandSourceStack commandSourceStack) {
        return (WorldData) commandSourceStack.getLevel().serverLevelData; // CraftBukkit - SPIGOT-6667: Use world specific function timer
    }
}
