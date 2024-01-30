package dev.tonimatas.cerium.mixins.world.item;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.bukkit.craftbukkit.v1_20_R3.event.CraftEventFactory;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(CrossbowItem.class)
public class CrossbowItemMixin {
    /**
     * @author TonimatasDEV
     * @reason CraftBukkit
     */
    @Overwrite
    private static void shootProjectile(Level level, LivingEntity livingEntity, InteractionHand interactionHand, ItemStack itemStack, ItemStack itemStack2, float f, boolean bl, float g, float h, float i) {
        if (!level.isClientSide) {
            boolean bl2 = itemStack2.is(Items.FIREWORK_ROCKET);
            Object projectile;
            if (bl2) {
                projectile = new FireworkRocketEntity(level, itemStack2, livingEntity, livingEntity.getX(), livingEntity.getEyeY() - 0.15000000596046448, livingEntity.getZ(), true);
            } else {
                projectile = CrossbowItem.getArrow(level, livingEntity, itemStack, itemStack2);
                if (bl || i != 0.0F) {
                    ((AbstractArrow)projectile).pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                }
            }

            if (livingEntity instanceof CrossbowAttackMob) {
                CrossbowAttackMob crossbowAttackMob = (CrossbowAttackMob)livingEntity;
                crossbowAttackMob.shootCrossbowProjectile(crossbowAttackMob.getTarget(), itemStack, (Projectile)projectile, i);
            } else {
                Vec3 vec3 = livingEntity.getUpVector(1.0F);
                Quaternionf quaternionf = (new Quaternionf()).setAngleAxis((double)(i * 0.017453292F), vec3.x, vec3.y, vec3.z);
                Vec3 vec32 = livingEntity.getViewVector(1.0F);
                Vector3f vector3f = vec32.toVector3f().rotate(quaternionf);
                ((Projectile)projectile).shoot((double)vector3f.x(), (double)vector3f.y(), (double)vector3f.z(), g, h);
            }

            // CraftBukkit start
            EntityShootBowEvent event = CraftEventFactory.callEntityShootBowEvent(livingEntity, itemStack, itemStack2, (Entity) projectile, livingEntity.getUsedItemHand(), f, true);
            if (event.isCancelled()) {
                event.getProjectile().remove();
                return;
            }
            // CraftBukkit end
            
            itemStack.hurtAndBreak(bl2 ? 3 : 1, livingEntity, (livingEntityx) -> {
                livingEntityx.broadcastBreakEvent(interactionHand);
            });
            // CraftBukkit start
            if (event.getProjectile() == ((Entity) projectile.getBukkitEntity())) {
                if (!level.addFreshEntity((Entity) projectile)) {
                    if (livingEntity instanceof ServerPlayer) {
                        ((ServerPlayer) livingEntity).getBukkitEntity().updateInventory();
                    }
                    return;
                }
            }
            // CraftBukkit end
            
            level.playSound((Player)null, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), SoundEvents.CROSSBOW_SHOOT, SoundSource.PLAYERS, 1.0F, f);
        }
    }
}
