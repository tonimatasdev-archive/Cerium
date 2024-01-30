package dev.tonimatas.cerium.mixins.world.item;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.item.EnderpearlItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(EnderpearlItem.class)
public class EnderpearlItemMixin {
    /**
     * @author TonimatasDEV
     * @reason CraftBukkit
     */
    @Overwrite
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack itemStack = player.getItemInHand(interactionHand);
        // CraftBukkit start - change order
        if (!level.isClientSide) {
            ThrownEnderpearl thrownEnderpearl = new ThrownEnderpearl(level, player);
            thrownEnderpearl.setItem(itemStack);
            thrownEnderpearl.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F);
            level.addFreshEntity(thrownEnderpearl);
            if (!level.addFreshEntity(thrownEnderpearl)) {
                if (player instanceof ServerPlayer) {
                    ((ServerPlayer) player).getBukkitEntity().updateInventory();
                }
                return InteractionResultHolder.fail(itemStack);
            }
        }

        level.playSound((Player) null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENDER_PEARL_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
        player.getCooldowns().addCooldown((EnderpearlItem) (Object) this, 20);
        // CraftBukkit end

        player.awardStat(Stats.ITEM_USED.get((EnderpearlItem) (Object) this));
        if (!player.getAbilities().instabuild) {
            itemStack.shrink(1);
        }

        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide());
    }
}
