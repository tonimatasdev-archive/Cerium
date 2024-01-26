package dev.tonimatas.cerium.mixins.world.item;

import dev.tonimatas.cerium.bridge.world.level.LevelBridge;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.item.HangingEntityItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_20_R3.CraftEquipmentSlot;
import org.bukkit.craftbukkit.v1_20_R3.block.CraftBlock;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(HangingEntityItem.class)
public class HangingEntityItemMixin {
    @Inject(method = "useOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/decoration/HangingEntity;playPlacementSound()V"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void cerium$useOn(UseOnContext useOnContext, CallbackInfoReturnable<InteractionResult> cir, BlockPos blockPos, Direction direction, BlockPos blockPos2, net.minecraft.world.entity.player.Player player, ItemStack itemStack, Level level, HangingEntity hangingEntity, CompoundTag compoundTag) {
        // CraftBukkit start - fire HangingPlaceEvent
        Player who = (useOnContext.getPlayer() == null) ? null : (Player) useOnContext.getPlayer().getBukkitEntity();
        Block blockClicked = ((LevelBridge) level).getWorld().getBlockAt(blockPos.getX(), blockPos.getY(), blockPos.getZ());
        BlockFace blockFace = CraftBlock.notchToBlockFace(direction);
        EquipmentSlot hand = CraftEquipmentSlot.getHand(useOnContext.getHand());

        HangingPlaceEvent event = new HangingPlaceEvent((org.bukkit.entity.Hanging) ((HangingEntity) hangingEntity).getBukkitEntity(), who, blockClicked, blockFace, hand, CraftItemStack.asBukkitCopy(itemStack));
        ((LevelBridge) level).getCraftServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            cir.setReturnValue(InteractionResult.FAIL);
        }
        // CraftBukkit end
    }
}
