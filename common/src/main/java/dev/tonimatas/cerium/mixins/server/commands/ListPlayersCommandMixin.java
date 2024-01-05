package dev.tonimatas.cerium.mixins.server.commands;

import dev.tonimatas.cerium.bridge.commands.CommandSourceBridge;
import dev.tonimatas.cerium.bridge.commands.CommandSourceStackBridge;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.server.commands.ListPlayersCommand;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.List;
import java.util.function.Function;

@Mixin(ListPlayersCommand.class)
public class ListPlayersCommandMixin {
    /**
     * @author TonimatasDEV
     * @reason CraftBukkit
     */
    @Overwrite
    private static int format(CommandSourceStack commandSourceStack, Function<ServerPlayer, Component> function) {
        PlayerList playerList = commandSourceStack.getServer().getPlayerList();
        // CraftBukkit start
        List<ServerPlayer> players = playerList.getPlayers();
        if (((CommandSourceStackBridge) commandSourceStack).bridge$getBukkitSender() instanceof org.bukkit.entity.Player) {
            org.bukkit.entity.Player sender = (org.bukkit.entity.Player) ((CommandSourceStackBridge) commandSourceStack).bridge$getBukkitSender();
            players = players.stream().filter((ep) -> sender.canSee(ep.getBukkitEntity())).collect(java.util.stream.Collectors.toList());
        }
        List<ServerPlayer> list = players;
        // CraftBukkit end
        Component component = ComponentUtils.formatList(list, function);
        commandSourceStack.sendSuccess(() -> {
            return Component.translatable("commands.list.players", new Object[]{list.size(), playerList.getMaxPlayers(), component});
        }, false);
        return list.size();
    }
}
