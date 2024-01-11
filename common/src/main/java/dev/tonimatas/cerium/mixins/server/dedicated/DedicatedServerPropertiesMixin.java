package dev.tonimatas.cerium.mixins.server.dedicated;

import dev.tonimatas.cerium.bridge.server.dedicated.DedicatedServerPropertiesBridge;
import dev.tonimatas.cerium.util.CeriumValues;
import joptsimple.OptionSet;
import net.minecraft.server.dedicated.DedicatedServerProperties;
import net.minecraft.server.dedicated.Settings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.nio.file.Path;

@Mixin(DedicatedServerProperties.class)
public class DedicatedServerPropertiesMixin implements DedicatedServerPropertiesBridge {
    @Unique public final boolean debug = ((Settings) (Object) this).get("debug", false); // CraftBukkit

    @Override
    public DedicatedServerProperties fromFile(Path path, OptionSet optionset) {
        CeriumValues.optionSet.set(optionset); // Cerium
        return DedicatedServerProperties.fromFile(path);
    }

    @Override
    public boolean getDebug() {
        return debug;
    }
}
