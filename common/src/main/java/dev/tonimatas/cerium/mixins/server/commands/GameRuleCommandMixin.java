package dev.tonimatas.cerium.mixins.server.commands;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.commands.GameRuleCommand;
import net.minecraft.world.level.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GameRuleCommand.class)
public class GameRuleCommandMixin {
    @Redirect(method = "setRule", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;getGameRules()Lnet/minecraft/world/level/GameRules;"))
    private static GameRules cerium$setRule(MinecraftServer instance, @Local CommandSourceStack commandSourceStack) {
        return commandSourceStack.getLevel().getGameRules();
    }

    @Redirect(method = "queryRule", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;getGameRules()Lnet/minecraft/world/level/GameRules;"))
    private static GameRules cerium$queryRule(MinecraftServer instance, @Local CommandSourceStack commandSourceStack) {
        return commandSourceStack.getLevel().getGameRules();
    }
}
