package dev.tonimatas.cerium.mixins.world.entity.item;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ItemEntity.class)
public class ItemEntityMixin {
    @Unique private int lastTick = MinecraftServer.currentTick - 1; // CraftBukkit
}
