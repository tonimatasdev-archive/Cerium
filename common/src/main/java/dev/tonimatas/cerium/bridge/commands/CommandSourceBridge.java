package dev.tonimatas.cerium.bridge.commands;

import net.minecraft.commands.CommandSourceStack;
import org.bukkit.command.CommandSender;

public interface CommandSourceBridge {
    CommandSender bridge$getBukkitSender(CommandSourceStack wrapper);
}
