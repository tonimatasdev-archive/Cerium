package dev.tonimatas.cerium.mixins.commands.arguments.selector;

import dev.tonimatas.cerium.bridge.commands.CommandSourceStackBridge;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.selector.EntitySelector;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntitySelector.class)
public class EntitySelectorMixin {
    @Redirect(method = "checkPermissions", at = @At(value = "INVOKE", target = "Lnet/minecraft/commands/CommandSourceStack;hasPermission(I)Z"))
    public boolean cerium$checkPermissions(CommandSourceStack instance, int i) {
        return ((CommandSourceStackBridge) instance).bridge$hasPermission(2, "minecraft.command.selector");
    }
}
