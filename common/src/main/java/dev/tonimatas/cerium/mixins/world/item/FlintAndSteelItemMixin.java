package dev.tonimatas.cerium.mixins.world.item;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FlintAndSteelItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.v1_20_R3.event.CraftEventFactory;
import org.bukkit.event.block.BlockIgniteEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(FlintAndSteelItem.class)
public class FlintAndSteelItemMixin {
    @Inject(method = "useOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;playSound(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/core/BlockPos;Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V", ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void cerium$useOn$1(UseOnContext useOnContext, CallbackInfoReturnable<InteractionResult> cir, Player player, Level level, BlockPos blockPos, BlockState blockState) {
        // CraftBukkit start - Store the clicked block
        if (CraftEventFactory.callBlockIgniteEvent(level, blockPos.relative(useOnContext.getClickedFace()), BlockIgniteEvent.IgniteCause.FLINT_AND_STEEL, player).isCancelled()) {
            useOnContext.getItemInHand().hurtAndBreak(1, player, (entityhuman1) -> {
                entityhuman1.broadcastBreakEvent(useOnContext.getHand());
            });
            cir.setReturnValue(InteractionResult.PASS);
        }
        // CraftBukkit end
    }

    @Inject(method = "useOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;playSound(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/core/BlockPos;Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V", ordinal = 1), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void cerium$useOn$2(UseOnContext useOnContext, CallbackInfoReturnable<InteractionResult> cir, Player player, Level level, BlockPos blockPos, BlockState blockState) {
        // CraftBukkit start - Store the clicked block
        if (CraftEventFactory.callBlockIgniteEvent(level, blockPos, BlockIgniteEvent.IgniteCause.FLINT_AND_STEEL, player).isCancelled()) {
            useOnContext.getItemInHand().hurtAndBreak(1, player, (entityhuman1) -> {
                entityhuman1.broadcastBreakEvent(useOnContext.getHand());
            });
            cir.setReturnValue(InteractionResult.PASS);
        }
        // CraftBukkit end
    }
}
