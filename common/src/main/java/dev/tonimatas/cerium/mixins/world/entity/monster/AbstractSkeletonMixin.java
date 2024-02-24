package dev.tonimatas.cerium.mixins.world.entity.monster;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(AbstractSkeleton.class)
public class AbstractSkeletonMixin {
    /**
     * @author TonimatasDEV
     * @reason CraftBukkit
     */
    @Overwrite
    public void performRangedAttack(LivingEntity livingEntity, float f) {
        ItemStack itemStack = ((AbstractSkeleton) (Object) this).getProjectile(((AbstractSkeleton) (Object) this).getItemInHand(ProjectileUtil.getWeaponHoldingHand(((AbstractSkeleton) (Object) this), Items.BOW)));
        AbstractArrow abstractArrow = ((AbstractSkeleton) (Object) this).getArrow(itemStack, f);
        double d = livingEntity.getX() - ((AbstractSkeleton) (Object) this).getX();
        double e = livingEntity.getY(0.3333333333333333) - abstractArrow.getY();
        double g = livingEntity.getZ() - ((AbstractSkeleton) (Object) this).getZ();
        double h = Math.sqrt(d * d + g * g);
        abstractArrow.shoot(d, e + h * 0.20000000298023224, g, 1.6F, (float)(14 - ((AbstractSkeleton) (Object) this).level().getDifficulty().getId() * 4));
        ((AbstractSkeleton) (Object) this).playSound(SoundEvents.SKELETON_SHOOT, 1.0F, 1.0F / (((AbstractSkeleton) (Object) this).getRandom().nextFloat() * 0.4F + 0.8F));
        ((AbstractSkeleton) (Object) this).level().addFreshEntity(abstractArrow);
    }
}
