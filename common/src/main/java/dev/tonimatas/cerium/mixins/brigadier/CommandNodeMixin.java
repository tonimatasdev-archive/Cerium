package dev.tonimatas.cerium.mixins.brigadier;

import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.tonimatas.cerium.bridge.brigadier.CommandNodeBridge;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(CommandNode.class)
public class CommandNodeMixin<S> implements CommandNodeBridge {
    @Shadow @Final private Map<String, CommandNode<S>> children;
    @Shadow @Final private Map<String, LiteralCommandNode<S>> literals;
    @Shadow @Final private Map<String, ArgumentCommandNode<S, ?>> arguments;

    @Override
    public void bridge$removeCommand(String name) {
        this.children.remove(name);
        this.literals.remove(name);
        this.arguments.remove(name);
    }
}
