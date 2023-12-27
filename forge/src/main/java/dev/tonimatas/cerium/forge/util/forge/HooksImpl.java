package dev.tonimatas.cerium.forge.util.forge;

@SuppressWarnings("unused")
public class HooksImpl {
    public static boolean isFabric() {
        return false;
    }

    public static boolean isForge() {
        return true;
    }

    public static boolean isNeoForge() {
        return false;
    }
}
