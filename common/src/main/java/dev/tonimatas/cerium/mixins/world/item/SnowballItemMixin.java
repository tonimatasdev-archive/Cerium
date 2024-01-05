package dev.tonimatas.cerium.mixins.world.item;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SnowballItem;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(SnowballItem.class)
public class SnowballItemMixin {
    /**
     * @author TonimatasDEV
     * @reason CraftBukkit
     */
    @Overwrite
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack itemStack = player.getItemInHand(interactionHand);
        // CraftBukkit - moved down
        //level.playSound((Player)null, player.getX(), player.getY(), player.getZ(), SoundEvents.SNOWBALL_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
        if (!level.isClientSide) {
            Snowball snowball = new Snowball(level, player);
            snowball.setItem(itemStack);
            snowball.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F);
            if (level.addFreshEntity(snowball)) {
                if (!player.getAbilities().instabuild) {
                    itemStack.shrink(1);
                }

                level.playSound((Player) null, player.getX(), player.getY(), player.getZ(), SoundEvents.SNOWBALL_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
            } else if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.getBukkitEntity().updateInventory();
            }
        }
        // CraftBukkit end

        player.awardStat(Stats.ITEM_USED.get((SnowballItem) (Object) this));
        // CraftBukkit start - moved up
        /*
        if (!player.getAbilities().instabuild) {
            itemStack.shrink(1);
        }
        */

        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide());
    }
}
