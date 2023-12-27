package dev.tonimatas.cerium.mixins.network.chat;

import dev.tonimatas.cerium.bridge.network.chat.TextColorBridge;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(TextColor.class)
public class TextColorMixin implements TextColorBridge {
    @Unique @Nullable public ChatFormatting format;

    @Override
    public ChatFormatting bridge$getFormat() {
        return this.format;
    }

    @Inject(method = "<init>(ILjava/lang/String;)V", at = @At("RETURN"))
    private void cerium$init(int color, String name, CallbackInfo ci) {
        this.format = ChatFormatting.getByName(name);
    }
}
