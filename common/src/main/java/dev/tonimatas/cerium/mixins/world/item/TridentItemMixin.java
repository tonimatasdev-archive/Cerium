package dev.tonimatas.cerium.mixins.world.item;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(TridentItem.class)
public abstract class TridentItemMixin {
    @Shadow public abstract int getUseDuration(ItemStack itemStack);

    /**
     * @author TonimatasDEV
     * @reason CraftBukkit
     */
    @Overwrite
    public void releaseUsing(ItemStack itemStack, Level level, LivingEntity livingEntity, int i) {
        if (livingEntity instanceof Player player) {
            int j = this.getUseDuration(itemStack) - i;
            if (j >= 10) {
                int k = EnchantmentHelper.getRiptide(itemStack);
                if (k <= 0 || player.isInWaterOrRain()) {
                    if (!level.isClientSide) {
                        // CraftBukkit - moved down
                        /*
                        itemStack.hurtAndBreak(1, player, (playerx) -> {
                            playerx.broadcastBreakEvent(livingEntity.getUsedItemHand());
                        });
                        */
                        if (k == 0) {
                            ThrownTrident thrownTrident = new ThrownTrident(level, player, itemStack);
                            thrownTrident.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 2.5F + (float)k * 0.5F, 1.0F);
                            if (player.getAbilities().instabuild) {
                                thrownTrident.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                            }

                            // CraftBukkit start
                            if (!level.addFreshEntity(thrownTrident)) {
                                if (player instanceof ServerPlayer serverPlayer) {
                                    serverPlayer.getBukkitEntity().updateInventory();
                                }
                                return;
                            }

                            itemStack.hurtAndBreak(1, player, (entityhuman1) -> {
                                entityhuman1.broadcastBreakEvent(livingEntity.getUsedItemHand());
                            });
                            thrownTrident.pickupItemStack = itemStack.copy(); // SPIGOT-4511 update since damage call moved
                            // CraftBukkit end
                            level.playSound((Player)null, thrownTrident, SoundEvents.TRIDENT_THROW, SoundSource.PLAYERS, 1.0F, 1.0F);
                            if (!player.getAbilities().instabuild) {
                                player.getInventory().removeItem(itemStack);
                            }
                            // CraftBukkit start - SPIGOT-5458 also need in this branch :(
                        } else {
                            itemStack.hurtAndBreak(1, player, (entityhuman1) -> {
                                entityhuman1.broadcastBreakEvent(livingEntity.getUsedItemHand());
                            });
                            // CraftBukkkit end
                        }
                    }

                    player.awardStat(Stats.ITEM_USED.get((TridentItem) (Object) this));
                    if (k > 0) {
                        // CraftBukkit start
                        org.bukkit.event.player.PlayerRiptideEvent event = new org.bukkit.event.player.PlayerRiptideEvent((org.bukkit.entity.Player) player.getBukkitEntity(), org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack.asCraftMirror(itemStack));
                        event.getPlayer().getServer().getPluginManager().callEvent(event);
                        // CraftBukkit end
                        float f = player.getYRot();
                        float g = player.getXRot();
                        float h = -Mth.sin(f * 0.017453292F) * Mth.cos(g * 0.017453292F);
                        float l = -Mth.sin(g * 0.017453292F);
                        float m = Mth.cos(f * 0.017453292F) * Mth.cos(g * 0.017453292F);
                        float n = Mth.sqrt(h * h + l * l + m * m);
                        float o = 3.0F * ((1.0F + (float)k) / 4.0F);
                        h *= o / n;
                        l *= o / n;
                        m *= o / n;
                        player.push((double)h, (double)l, (double)m);
                        player.startAutoSpinAttack(20);
                        if (player.onGround()) {
                            float p = 1.1999999F;
                            player.move(MoverType.SELF, new Vec3(0.0, 1.1999999284744263, 0.0));
                        }

                        SoundEvent soundEvent;
                        if (k >= 3) {
                            soundEvent = SoundEvents.TRIDENT_RIPTIDE_3;
                        } else if (k == 2) {
                            soundEvent = SoundEvents.TRIDENT_RIPTIDE_2;
                        } else {
                            soundEvent = SoundEvents.TRIDENT_RIPTIDE_1;
                        }

                        level.playSound((Player)null, player, soundEvent, SoundSource.PLAYERS, 1.0F, 1.0F);
                    }

                }
            }
        }
    }
}
