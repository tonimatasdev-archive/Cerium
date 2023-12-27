package dev.tonimatas.cerium.util;

import dev.architectury.injectables.annotations.ExpectPlatform;
import org.apache.commons.lang3.NotImplementedException;

public class Hooks {
    @ExpectPlatform
    public static boolean isFabric() {
        throw new NotImplementedException();
    }

    @ExpectPlatform
    public static boolean isForge() {
        throw new NotImplementedException();
    }

    @ExpectPlatform
    public static boolean isNeoForge() {
        throw new NotImplementedException();
    }
}
