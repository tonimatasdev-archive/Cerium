package dev.tonimatas.cerium.neoforge;

import dev.tonimatas.cerium.Cerium;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Cerium.MOD_ID)
public class CeriumNeoForge {
    public CeriumNeoForge() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        Cerium.init();
        eventBus.register(this);
    }
}