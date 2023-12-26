package dev.tonimatas.cerium.mixins.world.level;

import dev.tonimatas.cerium.bridge.world.level.LevelWriterBridge;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelWriter;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LevelWriter.class)
public interface LevelWriterMixin extends LevelWriterBridge {
    @Override
    default boolean bridge$addFreshEntity(Entity entity, org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason reason) {
        return false;
    }
}
