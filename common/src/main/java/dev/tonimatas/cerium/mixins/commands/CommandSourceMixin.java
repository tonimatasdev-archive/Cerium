package dev.tonimatas.cerium.mixins.commands;

import dev.tonimatas.cerium.bridge.commands.CommandSourceBridge;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import org.bukkit.command.CommandSender;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(CommandSource.class)
public interface CommandSourceMixin extends CommandSourceBridge {
    @Shadow @Final
    CommandSource NULL = null;

    default CommandSender bridge$getBukkitSender(CommandSourceStack wrapper) {
        return ((CommandSourceBridge) NULL).bridge$getBukkitSender(wrapper);
    }
}
