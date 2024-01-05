package dev.tonimatas.cerium.mixins.server.commands;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.commands.DifficultyCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(DifficultyCommand.class)
public class DifficultyCommandMixin {
    /**
     * @author TonimatasDEV
     * @reason CraftBukkit
     */
    @Overwrite
    public static int setDifficulty(CommandSourceStack commandSourceStack, Difficulty difficulty) throws CommandSyntaxException {
        MinecraftServer minecraftServer = commandSourceStack.getServer();
        ServerLevel worldServer = commandSourceStack.getLevel(); // CraftBukkit
        if (worldServer.getDifficulty() == difficulty) { // CraftBukkit
            throw DifficultyCommand.ERROR_ALREADY_DIFFICULT.create(difficulty.getKey());
        } else {
            minecraftServer.setDifficulty(difficulty, true);
            worldServer.serverLevelData.setDifficulty(difficulty); // CraftBukkit
            commandSourceStack.sendSuccess(() -> {
                return Component.translatable("commands.difficulty.success", new Object[]{difficulty.getDisplayName()});
            }, true);
            return 0;
        }
    }
}
