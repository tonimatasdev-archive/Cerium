package dev.tonimatas.cerium.mixins.world.entity.monster;

import net.minecraft.world.entity.monster.Skeleton;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Skeleton.class)
public class SkeletonMixin {
    @Inject(method = "doFreezeConversion", at = @At("HEAD"))
    private void cerium$doFreezeConversion(CallbackInfo ci) {
        ((Skeleton) (Object) this).cerium$addConvertToReasons(org.bukkit.event.entity.EntityTransformEvent.TransformReason.FROZEN, org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.FROZEN); // CraftBukkit - add spawn and transform reasons
    }
}
