package dev.tonimatas.cerium.util;

import net.minecraft.Util;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.AbstractProjectileDispenseBehavior;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class DoubleNestedClass {
    public static AbstractProjectileDispenseBehavior makeLingeringPotionDispenseBehavior(ItemStack itemStack) {
        return new AbstractProjectileDispenseBehavior() {
            protected Projectile getProjectile(Level level, Position position, ItemStack itemStack) {
                return (Projectile) Util.make(new ThrownPotion(level, position.x(), position.y(), position.z()), (thrownPotion) -> {
                    thrownPotion.setItem(itemStack);
                });
            }

            protected float getUncertainty() {
                return super.getUncertainty() * 0.5F;
            }

            protected float getPower() {
                return super.getPower() * 1.25F;
            }
        };
    }
    public static AbstractProjectileDispenseBehavior makeSplashPotionDispenseBehavior(ItemStack itemStack) {
        return new AbstractProjectileDispenseBehavior() {
            protected Projectile getProjectile(Level level, Position position, ItemStack itemStack) {
                return (Projectile) Util.make(new ThrownPotion(level, position.x(), position.y(), position.z()), (thrownPotion) -> {
                    thrownPotion.setItem(itemStack);
                });
            }

            protected float getUncertainty() {
                return super.getUncertainty() * 0.5F;
            }

            protected float getPower() {
                return super.getPower() * 1.25F;
            }
        };
    }
}
