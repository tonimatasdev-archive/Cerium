package dev.tonimatas.cerium.bridge.commands;

import com.mojang.brigadier.tree.CommandNode;

public interface CommandSourceStackBridge {
    boolean bridge$hasPermission(int i, String bukkitPermission);
    
    void cerium$setCurrentCommand(CommandNode value);

    org.bukkit.command.CommandSender bridge$getBukkitSender();
}
