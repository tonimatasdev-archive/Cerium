package dev.tonimatas.cerium.mixins.world.level;

import dev.tonimatas.cerium.bridge.world.level.LevelAccessorBridge;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LevelAccessor.class)
public interface LevelAccessorMixin extends LevelAccessorBridge {
    default ServerLevel bridge$getMinecraftWorld() {
        return this.bridge$getMinecraftWorld();
    }
}
