package dev.tonimatas.cerium.mixins.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.tonimatas.cerium.bridge.commands.arguments.EntityArgumentBridge;
import dev.tonimatas.cerium.bridge.commands.arguments.selector.EntitySelectorParserBridge;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.concurrent.atomic.AtomicBoolean;

@Mixin(EntityArgument.class)
public abstract class EntityArgumentMixin implements EntityArgumentBridge {
    @Shadow public abstract EntitySelector parse(StringReader stringReader) throws CommandSyntaxException;

    @Unique private AtomicBoolean cerium$overridePermissions;

    @Override
    public EntitySelector bridge$parse(StringReader stringreader, boolean overridePermissions) throws CommandSyntaxException {
        cerium$overridePermissions.set(overridePermissions);
        return this.parse(stringreader);
    }

    @Redirect(method = "parse(Lcom/mojang/brigadier/StringReader;)Lnet/minecraft/commands/arguments/selector/EntitySelector;", at = @At(value = "INVOKE", target = "Lnet/minecraft/commands/arguments/selector/EntitySelectorParser;parse()Lnet/minecraft/commands/arguments/selector/EntitySelector;"))
    public EntitySelector cerium$parse(EntitySelectorParser instance) throws CommandSyntaxException {
        return ((EntitySelectorParserBridge) instance).bridge$parse(cerium$overridePermissions.getAndSet(false));
    }
}
