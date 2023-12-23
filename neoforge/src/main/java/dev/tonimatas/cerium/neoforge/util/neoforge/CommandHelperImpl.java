package dev.tonimatas.cerium.neoforge.util.neoforge;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.server.command.CommandHelper;

import java.util.Map;
import java.util.function.Function;

@SuppressWarnings("unused")
public class CommandHelperImpl {
    public static <S, T> void mergeCommandNode(CommandNode<S> sourceNode, CommandNode<T> resultNode, Map<CommandNode<S>, CommandNode<T>> sourceToResult, S canUse, Command<T> execute, Function<SuggestionProvider<S>, SuggestionProvider<T>> sourceToResultSuggestion) {
        CommandHelper.mergeCommandNode(sourceNode, resultNode, sourceToResult, canUse, execute, sourceToResultSuggestion);
    }
}
