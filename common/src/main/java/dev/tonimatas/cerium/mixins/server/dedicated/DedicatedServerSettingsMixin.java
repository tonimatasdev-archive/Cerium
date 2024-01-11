package dev.tonimatas.cerium.mixins.server.dedicated;

import dev.tonimatas.cerium.bridge.server.dedicated.DedicatedServerSettingsBridge;
import dev.tonimatas.cerium.util.CeriumValues;
import joptsimple.OptionSet;
import net.minecraft.server.dedicated.DedicatedServerProperties;
import net.minecraft.server.dedicated.DedicatedServerSettings;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import java.io.File;
import java.nio.file.Path;

@Mixin(DedicatedServerSettings.class)
public class DedicatedServerSettingsMixin implements DedicatedServerSettingsBridge {
    @Shadow @Final @Mutable
    private Path source;

    @Shadow private DedicatedServerProperties properties;

    @Override
    public void cerium$constructor(OptionSet optionSet) {
        this.source = ((File) optionSet.valueOf("config")).toPath();
        CeriumValues.optionSet.set(optionSet); // Cerium
        this.properties = DedicatedServerProperties.fromFile(source);
    }
}
