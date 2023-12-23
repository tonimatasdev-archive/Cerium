package dev.tonimatas.cerium.bridge.commands.arguments.selector;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.arguments.selector.EntitySelector;

public interface EntitySelectorParserBridge {
    void bridge$parseSelector(boolean overridePermissions) throws CommandSyntaxException;
    EntitySelector bridge$parse(boolean overridePermissions) throws CommandSyntaxException;
}
