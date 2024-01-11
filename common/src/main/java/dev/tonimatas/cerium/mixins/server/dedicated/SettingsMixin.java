package dev.tonimatas.cerium.mixins.server.dedicated;

import dev.tonimatas.cerium.util.CeriumValues;
import joptsimple.OptionSet;
import net.minecraft.server.dedicated.Settings;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.nio.file.Path;
import java.util.Properties;

@Mixin(Settings.class)
public abstract class SettingsMixin {
    @Shadow @Nullable protected abstract String getStringRaw(String string);

    @Shadow @Final public Properties properties;
    @Unique private OptionSet options = null;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void cerium$init(Properties properties, CallbackInfo ci) {
        options = CeriumValues.optionSet.get();
    }

    @Unique
    private String getOverride(String name, String value) {
        if ((this.options != null) && (this.options.has(name))) {
            return String.valueOf(this.options.valueOf(name));
        }

        return value;
    }

    @Inject(method = "loadFromFile", at = @At(value = "HEAD"), cancellable = true)
    private static void cerium$loadFromFile(Path path, CallbackInfoReturnable<Properties> cir) {
        // CraftBukkit start - SPIGOT-7465, MC-264979: Don't load if file doesn't exist
        if (!path.toFile().exists()) {
            cir.setReturnValue(new Properties());
        }
        // CraftBukkit end
    }

    @Inject(method = "store", at = @At(value = "HEAD"), cancellable = true)
    private static void cerium$store(Path path, CallbackInfo ci) {
        // CraftBukkit start - Don't attempt writing to file if it's read only
        if (path.toFile().exists() && !path.toFile().canWrite()) {
            ci.cancel();
        }
        // CraftBukkit end
    }

    @Redirect(method = "getStringRaw", at = @At(value = "INVOKE", target = "Ljava/util/Properties;get(Ljava/lang/Object;)Ljava/lang/Object;"))
    private Object cerium$getStringRaw(Properties instance, Object key, String s) {
        return getOverride(s, this.properties.getProperty(s)); // CraftBukkit
    }
}
