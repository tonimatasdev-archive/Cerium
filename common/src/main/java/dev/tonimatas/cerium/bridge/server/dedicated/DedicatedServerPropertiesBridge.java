package dev.tonimatas.cerium.bridge.server.dedicated;

import joptsimple.OptionSet;
import net.minecraft.server.dedicated.DedicatedServerProperties;

import java.nio.file.Path;

public interface DedicatedServerPropertiesBridge {

    DedicatedServerProperties fromFile(Path path, OptionSet optionset);

    boolean getDebug();
}
