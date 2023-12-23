package dev.tonimatas.cerium.bridge.commands;

public interface CommandSourceStackBridge {
    boolean bridge$hasPermission(int i, String bukkitPermission);

    org.bukkit.command.CommandSender bridge$getBukkitSender();
}
