package dev.tonimatas.cerium.bridge.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.arguments.selector.EntitySelector;

public interface EntityArgumentBridge {
    EntitySelector bridge$parse(StringReader stringreader, boolean overridePermissions) throws CommandSyntaxException;
}
