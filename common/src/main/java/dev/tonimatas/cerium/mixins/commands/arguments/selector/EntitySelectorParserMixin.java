package dev.tonimatas.cerium.mixins.commands.arguments.selector;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.tonimatas.cerium.bridge.commands.arguments.selector.EntitySelectorParserBridge;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntitySelectorParser.class)
public abstract class EntitySelectorParserMixin implements EntitySelectorParserBridge {
    @Shadow private boolean usesSelectors;
    @Shadow protected abstract void shadow$parseSelector() throws CommandSyntaxException;
    @Shadow public abstract net.minecraft.commands.arguments.selector.EntitySelector parse() throws CommandSyntaxException;

    @Unique private Boolean cerium$overridePermissions;

    @Override
    public EntitySelector bridge$parse(boolean overridePermissions) throws CommandSyntaxException {
        return this.cerium$parse(overridePermissions);
    }

    @Unique
    public EntitySelector cerium$parse(boolean overridePermissions) throws CommandSyntaxException {
        try {
            this.cerium$overridePermissions = overridePermissions;
            return this.parse();
        } finally {
            this.cerium$overridePermissions = null;
        }
    }

    @Override
    public void bridge$parseSelector(boolean overridePermissions) throws CommandSyntaxException {
        this.cerium$parseSelector(overridePermissions);
    }

    @Unique
    public void cerium$parseSelector(boolean overridePermissions) throws CommandSyntaxException {
        this.usesSelectors = !overridePermissions;
        this.shadow$parseSelector();
    }

    @Inject(method = "parseSelector", at = @At("HEAD"))
    public void cerium$onParserSelector(CallbackInfo ci) {
        if (this.cerium$overridePermissions != null) {
            this.usesSelectors = !this.cerium$overridePermissions;
        }
    }
}