package dev.tonimatas.cerium.mixins.server.gui;

import net.minecraft.server.gui.MinecraftServerGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

@Mixin(MinecraftServerGui.class)
public class MinecraftServerGuiMixin {
    @Unique private static final java.util.regex.Pattern ANSI = java.util.regex.Pattern.compile("\\x1B\\[([0-9]{1,2}(;[0-9]{1,2})*)?[m|K]"); // CraftBukkit

    @Redirect(method = "print", at = @At(value = "INVOKE", target = "Ljavax/swing/text/Document;insertString(ILjava/lang/String;Ljavax/swing/text/AttributeSet;)V"))
    private void cerium$print(Document instance, int i, String string, AttributeSet attributeSet) throws BadLocationException {
        instance.insertString(instance.getLength(), ANSI.matcher(string).replaceAll(""), (AttributeSet) null); // CraftBukkit
    }
}
