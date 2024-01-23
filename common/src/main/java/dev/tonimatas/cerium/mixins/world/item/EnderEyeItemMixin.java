package dev.tonimatas.cerium.mixins.world.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.StructureTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.EyeOfEnder;
import net.minecraft.world.item.EnderEyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(EnderEyeItem.class)
public class EnderEyeItemMixin {
    /**
     * @author TonimatasDEV
     * @reason CraftBukkit
     */
    @Overwrite
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack itemStack = player.getItemInHand(interactionHand);
        BlockHitResult blockHitResult = Item.getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE);
        if (blockHitResult.getType() == HitResult.Type.BLOCK && level.getBlockState(blockHitResult.getBlockPos()).is(Blocks.END_PORTAL_FRAME)) {
            return InteractionResultHolder.pass(itemStack);
        } else {
            player.startUsingItem(interactionHand);
            if (level instanceof ServerLevel) {
                ServerLevel serverLevel = (ServerLevel)level;
                BlockPos blockPos = serverLevel.findNearestMapStructure(StructureTags.EYE_OF_ENDER_LOCATED, player.blockPosition(), 100, false);
                if (blockPos != null) {
                    EyeOfEnder eyeOfEnder = new EyeOfEnder(level, player.getX(), player.getY(0.5), player.getZ());
                    eyeOfEnder.setItem(itemStack);
                    eyeOfEnder.signalTo(blockPos);
                    level.gameEvent(GameEvent.PROJECTILE_SHOOT, eyeOfEnder.position(), GameEvent.Context.of(player));
                    // CraftBukkit start
                    if (!level.addFreshEntity(eyeOfEnder)) {
                        return new InteractionResultHolder<>(InteractionResult.FAIL, itemStack);
                    }
                    // CraftBukkit end
                    if (player instanceof ServerPlayer) {
                        CriteriaTriggers.USED_ENDER_EYE.trigger((ServerPlayer)player, blockPos);
                    }

                    level.playSound((Player)null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENDER_EYE_LAUNCH, SoundSource.NEUTRAL, 0.5F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
                    level.levelEvent((Player)null, 1003, player.blockPosition(), 0);
                    if (!player.getAbilities().instabuild) {
                        itemStack.shrink(1);
                    }

                    player.awardStat(Stats.ITEM_USED.get((EnderEyeItem) (Object) this));
                    player.swing(interactionHand, true);
                    return InteractionResultHolder.success(itemStack);
                }
            }

            return InteractionResultHolder.consume(itemStack);
        }
    }
}
