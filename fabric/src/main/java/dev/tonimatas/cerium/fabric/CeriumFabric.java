package dev.tonimatas.cerium.fabric;

import dev.tonimatas.cerium.Cerium;
import net.fabricmc.api.ModInitializer;

public class CeriumFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Cerium.init();
    }
}