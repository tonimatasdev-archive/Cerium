package dev.tonimatas.cerium.mixins.core.dispenser;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.dispenser.ShulkerBoxDispenseBehavior;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import org.bukkit.craftbukkit.v1_20_R3.block.CraftBlock;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
import org.bukkit.event.block.BlockDispenseEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShulkerBoxDispenseBehavior.class)
public class ShulkerBoxDispenseBehaviorMixin {
    @Inject(method = "execute", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;isEmptyBlock(Lnet/minecraft/core/BlockPos;)Z", shift = At.Shift.AFTER), cancellable = true)
    private void cerium$execute(BlockSource blockSource, ItemStack itemStack, CallbackInfoReturnable<ItemStack> cir, @Local BlockPos blockposition) {
        // CraftBukkit start
        org.bukkit.block.Block bukkitBlock = CraftBlock.at(blockSource.level(), blockSource.pos());
        CraftItemStack craftItem = CraftItemStack.asCraftMirror(itemStack);

        BlockDispenseEvent event = new BlockDispenseEvent(bukkitBlock, craftItem.clone(), new org.bukkit.util.Vector(blockposition.getX(), blockposition.getY(), blockposition.getZ()));
        if (!DispenserBlock.eventFired) {
            blockSource.level().getCraftServer().getPluginManager().callEvent(event);
        }

        if (event.isCancelled()) {
            cir.setReturnValue(itemStack);
        }

        if (!event.getItem().equals(craftItem)) {
            // Chain to handler for new item
            ItemStack eventStack = CraftItemStack.asNMSCopy(event.getItem());
            DispenseItemBehavior idispensebehavior = (DispenseItemBehavior) DispenserBlock.DISPENSER_REGISTRY.get(eventStack.getItem());
            if (idispensebehavior != DispenseItemBehavior.NOOP && idispensebehavior != this) {
                idispensebehavior.dispense(blockSource, eventStack);
                cir.setReturnValue(itemStack);
            }
        }
        // CraftBukkit end
    }
}
