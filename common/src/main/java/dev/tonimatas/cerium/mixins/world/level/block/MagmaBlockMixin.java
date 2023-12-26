package dev.tonimatas.cerium.mixins.world.level.block;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.MagmaBlock;
import org.bukkit.craftbukkit.v1_20_R3.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MagmaBlock.class)
public class MagmaBlockMixin {
    @Redirect(method = "stepOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    private boolean cerium$stepOn(Entity instance, DamageSource damageSource, float f, @Local Level level, @Local BlockPos blockPos) {
        CraftEventFactory.blockDamage = level.getWorld().getBlockAt(blockPos.getX(), blockPos.getY(), blockPos.getZ());
        boolean b = instance.hurt(level.damageSources().hotFloor(), 1.0F);
        CraftEventFactory.blockDamage = null;
        return b;
    }
}
