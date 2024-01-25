package dev.tonimatas.cerium.mixins.world.entity.monster;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(Drowned.class)
public class DrownedMixin {
    /**
     * @author TonimatasDEV
     * @reason CraftBukkit
     */
    @Overwrite
    public void performRangedAttack(LivingEntity livingEntity, float f) {
        ThrownTrident thrownTrident = new ThrownTrident(((LivingEntity) (Object) this).level(), ((LivingEntity) (Object) this), new ItemStack(Items.TRIDENT));
        double d = livingEntity.getX() - ((LivingEntity) (Object) this).getX();
        double e = livingEntity.getY(0.3333333333333333) - thrownTrident.getY();
        double g = livingEntity.getZ() - ((LivingEntity) (Object) this).getZ();
        double h = Math.sqrt(d * d + g * g);
        thrownTrident.shoot(d, e + h * 0.20000000298023224, g, 1.6F, (float)(14 - ((LivingEntity) (Object) this).level().getDifficulty().getId() * 4));
        ((LivingEntity) (Object) this).playSound(SoundEvents.DROWNED_SHOOT, 1.0F, 1.0F / (((LivingEntity) (Object) this).getRandom().nextFloat() * 0.4F + 0.8F));
        ((LivingEntity) (Object) this).level().addFreshEntity(thrownTrident);
    }
}
