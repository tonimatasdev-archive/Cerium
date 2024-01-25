package dev.tonimatas.cerium.mixins.world.entity.monster;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Ghast.GhastShootFireballGoal.class)
public class GhastMixin {
    @Shadow @Final private Ghast ghast;

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/LargeFireball;setPos(DDD)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void cerium$tick(CallbackInfo ci, LivingEntity livingEntity, double d, Level level, double e, Vec3 vec3, double f, double g, double h, LargeFireball largeFireball) {
        largeFireball.bukkitYield = largeFireball.explosionPower = this.ghast.getExplosionPower(); // CraftBukkit - set bukkitYield when setting explosionpower
    }
}
