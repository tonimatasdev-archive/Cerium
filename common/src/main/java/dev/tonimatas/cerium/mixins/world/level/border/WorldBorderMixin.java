package dev.tonimatas.cerium.mixins.world.level.border;

import dev.tonimatas.cerium.bridge.world.level.border.WorldBorderBridge;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.border.WorldBorder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(WorldBorder.class)
public class WorldBorderMixin implements WorldBorderBridge {
    @Unique public Level cerium$level;

    @Override
    public Level bridge$getWorld() {
        return this.cerium$level;
    }

    @Override
    public void bridge$setWorld(Level world) {
        this.cerium$level = world;
    }
}
