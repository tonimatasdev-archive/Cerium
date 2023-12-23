package dev.tonimatas.cerium.util;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.CommandNode;
import dev.architectury.injectables.annotations.ExpectPlatform;
import org.apache.commons.lang3.NotImplementedException;

import java.util.Map;
import java.util.function.Function;

public class CommandHelper {
    @ExpectPlatform
    public static <S, T> void mergeCommandNode(CommandNode<S> sourceNode, CommandNode<T> resultNode, Map<CommandNode<S>, CommandNode<T>> sourceToResult, S canUse, Command<T> execute, Function<SuggestionProvider<S>, SuggestionProvider<T>> sourceToResultSuggestion) {
        throw new NotImplementedException();
    }
}
