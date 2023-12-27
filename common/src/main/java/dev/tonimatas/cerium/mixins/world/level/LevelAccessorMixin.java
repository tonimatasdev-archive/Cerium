package dev.tonimatas.cerium.mixins.world.level;

import dev.tonimatas.cerium.bridge.world.level.LevelAccessorBridge;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;

@SuppressWarnings({"AddedMixinMembersNamePattern"})
@Mixin(LevelAccessor.class)
public interface LevelAccessorMixin extends LevelAccessorBridge {
    @Override
    default ServerLevel getMinecraftWorld() {
        return null;
    }
}
