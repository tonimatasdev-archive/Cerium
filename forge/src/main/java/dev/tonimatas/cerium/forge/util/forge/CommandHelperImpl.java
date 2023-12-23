package dev.tonimatas.cerium.forge.util.forge;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraftforge.server.command.CommandHelper;

import java.util.Map;
import java.util.function.Function;

@SuppressWarnings("unused")
public class CommandHelperImpl {
    public static <S, T> void mergeCommandNode(CommandNode<S> sourceNode, CommandNode<T> resultNode, Map<CommandNode<S>, CommandNode<T>> sourceToResult, S canUse, Command<T> execute, Function<SuggestionProvider<S>, SuggestionProvider<T>> sourceToResultSuggestion) {
        CommandHelper.mergeCommandNode(sourceNode, resultNode, sourceToResult, canUse, execute, sourceToResultSuggestion);
    }
}
