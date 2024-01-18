package dev.tonimatas.cerium.mixins.server.rcon;

import dev.tonimatas.cerium.bridge.server.rcon.RconConsoleSourceBridge;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.rcon.RconConsoleSource;
import org.bukkit.craftbukkit.v1_20_R3.command.CraftRemoteConsoleCommandSender;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.net.SocketAddress;

@Mixin(RconConsoleSource.class)
public class RconConsoleSourceMixin implements RconConsoleSourceBridge {
    @Shadow @Final private StringBuffer buffer;
    @Unique public SocketAddress socketAddress;
    @Unique private final CraftRemoteConsoleCommandSender remoteConsole = new CraftRemoteConsoleCommandSender((RconConsoleSource) (Object) this);


    @Override
    public void cerium$setSocketAddress(SocketAddress socketAddress) {
        this.socketAddress = socketAddress;
    }

    @Override
    public SocketAddress cerium$getSocketAddress() {
        return this.socketAddress;
    }

    public void sendMessage(String message) {
        this.buffer.append(message);
    }
    
    public org.bukkit.command.CommandSender getBukkitSender(CommandSourceStack wrapper) {
        return this.remoteConsole;
    }
}
