package dev.tonimatas.cerium.mixins.commands;

import dev.tonimatas.cerium.bridge.commands.CommandSourceBridge;
import net.minecraft.commands.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_20_R3.command.ServerCommandSender;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(targets = "net/minecraft/commands/CommandSource$1")
public class CommandSource1Mixin implements CommandSourceBridge {
    @Unique
    public CommandSender cerium$getBukkitSender(CommandSourceStack wrapper) {
        return new ServerCommandSender() {
            private final boolean isOp = wrapper.hasPermission(wrapper.getServer().getOperatorUserPermissionLevel());

            @Override
            public boolean isOp() {
                return isOp;
            }

            @Override
            public void setOp(boolean value) {
            }

            @Override
            public void sendMessage(@NotNull String message) {

            }

            @Override
            public void sendMessage(@NotNull String[] messages) {

            }

            @NotNull
            @Override
            public String getName() {
                return "NULL";
            }
        };
    }

    @Override
    public CommandSender bridge$getBukkitSender(CommandSourceStack wrapper) {
        return cerium$getBukkitSender(wrapper);
    }
}
