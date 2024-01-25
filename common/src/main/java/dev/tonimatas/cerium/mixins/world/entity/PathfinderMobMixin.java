package dev.tonimatas.cerium.mixins.world.entity;

import dev.tonimatas.cerium.bridge.world.entity.EntityBridge;
import dev.tonimatas.cerium.bridge.world.level.LevelBridge;
import net.minecraft.world.entity.PathfinderMob;
import org.bukkit.event.entity.EntityUnleashEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PathfinderMob.class)
public class PathfinderMobMixin {
    @Inject(method = "tickLeash", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/PathfinderMob;dropLeash(ZZ)V"))
    private void cerium$tickLeash(CallbackInfo ci) {
        ((LevelBridge) ((PathfinderMob) (Object) this).level()).getCraftServer().getPluginManager().callEvent(new EntityUnleashEvent(((EntityBridge) (Object) this).getBukkitEntity(), EntityUnleashEvent.UnleashReason.DISTANCE)); // CraftBukkit
    }
}
