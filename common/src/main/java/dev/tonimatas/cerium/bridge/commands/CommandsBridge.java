package dev.tonimatas.cerium.bridge.commands;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.context.ContextChain;
import net.minecraft.commands.CommandSourceStack;

public interface CommandsBridge {
    void bridge$dispatchServerCommand(CommandSourceStack sender, String command);

    void bridge$performPrefixedCommand(CommandSourceStack commandlistenerwrapper, String s, String label);

    void bridge$performCommand(ParseResults<CommandSourceStack> parseresults, String s, String label);

    static ContextChain<CommandSourceBridge> finishParsing(ParseResults<CommandSourceBridge> parseresults, String s, CommandSourceBridge commandlistenerwrapper, String label) {
        return null;
    }
}
