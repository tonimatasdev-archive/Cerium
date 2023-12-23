package dev.tonimatas.cerium.mixins.commands;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import dev.tonimatas.cerium.bridge.brigadier.CommandNodeBridge;
import dev.tonimatas.cerium.bridge.commands.CommandSourceStackBridge;
import dev.tonimatas.cerium.bridge.commands.CommandsBridge;
import dev.tonimatas.cerium.util.CommandHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ClientboundCommandsPacket;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Mixin(Commands.class)
public abstract class CommandsMixin implements CommandsBridge {
    @Shadow public abstract void performPrefixedCommand(CommandSourceStack arg, String string);

    @Shadow @Final private CommandDispatcher<CommandSourceStack> dispatcher;

    @Shadow public abstract void performCommand(ParseResults<CommandSourceStack> parseResults, String string);

    @Shadow protected abstract void fillUsableCommands(CommandNode<CommandSourceStack> commandNode, CommandNode<SharedSuggestionProvider> commandNode2, CommandSourceStack commandSourceStack, Map<CommandNode<CommandSourceStack>, CommandNode<SharedSuggestionProvider>> map);

    @Unique public AtomicReference<String> cerium$prefixedLabel;
    @Unique private static AtomicReference<String> cerium$performLabel;

    @Override
    public void bridge$dispatchServerCommand(CommandSourceStack sender, String command) {
        Joiner joiner = Joiner.on(" ");
        if (command.startsWith("/")) {
            command = command.substring(1);
        }

        ServerCommandEvent event = new ServerCommandEvent(((CommandSourceStackBridge) sender).bridge$getBukkitSender(), command);
        org.bukkit.Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }
        command = event.getCommand();

        String[] args = command.split(" ");

        String cmd = args[0];
        if (cmd.startsWith("minecraft:")) cmd = cmd.substring("minecraft:".length());
        if (cmd.startsWith("bukkit:")) cmd = cmd.substring("bukkit:".length());

        // Block disallowed commands
        if (cmd.equalsIgnoreCase("stop") || cmd.equalsIgnoreCase("kick") || cmd.equalsIgnoreCase("op")
                || cmd.equalsIgnoreCase("deop") || cmd.equalsIgnoreCase("ban") || cmd.equalsIgnoreCase("ban-ip")
                || cmd.equalsIgnoreCase("pardon") || cmd.equalsIgnoreCase("pardon-ip") || cmd.equalsIgnoreCase("reload")) {
            return;
        }

        // Handle vanilla commands;
        if (sender.getLevel().getCraftServer().getCommandBlockOverride(args[0])) {
            args[0] = "minecraft:" + args[0];
        }

        String newCommand = joiner.join(args);
        this.bridge$performPrefixedCommand(sender, newCommand, newCommand);
    }

    @Override
    public void bridge$performPrefixedCommand(CommandSourceStack commandlistenerwrapper, String s, String label) {
        this.cerium$prefixedLabel.set(label);
        this.performPrefixedCommand(commandlistenerwrapper, s);
    }

    @Inject(method = "performPrefixedCommand", at = @At(value = "INVOKE", target = "Lnet/minecraft/commands/Commands;performCommand(Lcom/mojang/brigadier/ParseResults;Ljava/lang/String;)V"), cancellable = true)
    public void cerium$performPrefixedCommand(CommandSourceStack commandSourceStack, String string, CallbackInfo ci) {
        if (!cerium$prefixedLabel.get().isEmpty()) {
            this.bridge$performCommand(this.dispatcher.parse(string, commandSourceStack), string, cerium$prefixedLabel.getAndSet(""));
            ci.cancel();
        }
    }

    @Override
    public void bridge$performCommand(ParseResults<CommandSourceStack> parseresults, String s, String label) {
        cerium$performLabel.set(label);
        this.performCommand(parseresults, s);
    }

    @Redirect(method = "finishParsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/chat/MutableComponent;withStyle(Lnet/minecraft/ChatFormatting;)Lnet/minecraft/network/chat/MutableComponent;"))
    private static MutableComponent inject(MutableComponent instance, ChatFormatting arg, @Local String string) {
        if (cerium$performLabel.get().isEmpty()) {
            string = "/" + string;
        } else {
            string = cerium$performLabel.get();
        }

        String finalString = string;
        return Component.empty().withStyle(ChatFormatting.GRAY).withStyle((style) -> style.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, finalString)));
    }

    /**
     * @author TonimatasDEV
     * @reason CraftBukkit
     */
    @Overwrite
    public void sendCommands(ServerPlayer p_82096_) {
        // CraftBukkit start
        // Register Vanilla commands into builtRoot as before
        Map<CommandNode<CommandSourceStack>, CommandNode<SharedSuggestionProvider>> map = Maps.newIdentityHashMap(); // Use identity to prevent aliasing issues
        RootCommandNode vanillaRoot = new RootCommandNode();

        RootCommandNode<CommandSourceStack> vanilla = p_82096_.server.vanillaCommandDispatcher.getDispatcher().getRoot();
        map.put(vanilla, vanillaRoot);
        this.fillUsableCommands(vanilla, vanillaRoot, p_82096_.createCommandSourceStack(), (Map) map);

        // Now build the global commands in a second pass
        RootCommandNode<SharedSuggestionProvider> rootcommandnode = new RootCommandNode<>();

        map.put(this.dispatcher.getRoot(), rootcommandnode);
        // FORGE: Use our own command node merging method to handle redirect nodes properly, see issue #7551
        CommandHelper.mergeCommandNode(this.dispatcher.getRoot(), rootcommandnode, map, p_82096_.createCommandSourceStack(), ctx -> 0, suggest -> SuggestionProviders.safelySwap((com.mojang.brigadier.suggestion.SuggestionProvider<SharedSuggestionProvider>) (com.mojang.brigadier.suggestion.SuggestionProvider<?>) suggest));

        Collection<String> bukkit = new LinkedHashSet<>();
        for (CommandNode node : rootcommandnode.getChildren()) {
            bukkit.add(node.getName());
        }

        PlayerCommandSendEvent event = new PlayerCommandSendEvent(p_82096_.getBukkitEntity(), new LinkedHashSet<>(bukkit));
        event.getPlayer().getServer().getPluginManager().callEvent(event);

        // Remove labels that were removed during the event
        for (String orig : bukkit) {
            if (!event.getCommands().contains(orig)) {
                ((CommandNodeBridge) rootcommandnode).bridge$removeCommand(orig);
            }
        }
        // CraftBukkit end

        p_82096_.connection.send(new ClientboundCommandsPacket(rootcommandnode));
    }
}
