package dev.tonimatas.cerium.forge;

import dev.tonimatas.cerium.Cerium;
import dev.tonimatas.cerium.mixins.world.entity.item.FallingBlockEntityMixin;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Cerium.MOD_ID)
public class CeriumForge {
    public CeriumForge() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        Cerium.init();
        eventBus.register(this);
    }
}