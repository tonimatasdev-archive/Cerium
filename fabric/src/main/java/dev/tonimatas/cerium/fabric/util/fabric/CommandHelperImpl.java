package dev.tonimatas.cerium.fabric.util.fabric;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.CommandNode;

import java.util.Map;
import java.util.function.Function;

@SuppressWarnings("unused")
public class CommandHelperImpl {
    public static <S, T> void mergeCommandNode(CommandNode<S> sourceNode, CommandNode<T> resultNode, Map<CommandNode<S>, CommandNode<T>> sourceToResult, S canUse, Command<T> execute, Function<SuggestionProvider<S>, SuggestionProvider<T>> sourceToResultSuggestion) {

    }
}
