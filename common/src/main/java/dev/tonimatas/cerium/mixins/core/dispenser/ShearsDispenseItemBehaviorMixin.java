package dev.tonimatas.cerium.mixins.core.dispenser;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.dispenser.ShearsDispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Shearable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import org.bukkit.craftbukkit.v1_20_R3.block.CraftBlock;
import org.bukkit.craftbukkit.v1_20_R3.event.CraftEventFactory;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
import org.bukkit.event.block.BlockDispenseEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShearsDispenseItemBehavior.class)
public class ShearsDispenseItemBehaviorMixin {
    @Unique private static CraftItemStack cerium$craftItem;
    @Unique private static org.bukkit.block.Block cerium$bukkitBlock;

    @Inject(method = "execute", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/dispenser/BlockSource;level()Lnet/minecraft/server/level/ServerLevel;", shift = At.Shift.AFTER), cancellable = true)
    private void cerium$execute(BlockSource blockSource, ItemStack itemStack, CallbackInfoReturnable<ItemStack> cir, @Local ServerLevel worldserver) {
        // CraftBukkit start
        org.bukkit.block.Block bukkitBlock = CraftBlock.at(worldserver, blockSource.pos());
        CraftItemStack craftItem = CraftItemStack.asCraftMirror(itemStack);

        BlockDispenseEvent event = new BlockDispenseEvent(bukkitBlock, craftItem.clone(), new org.bukkit.util.Vector(0, 0, 0));
        if (!DispenserBlock.eventFired) {
            worldserver.getCraftServer().getPluginManager().callEvent(event);
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

        cerium$bukkitBlock = bukkitBlock;
        cerium$craftItem = craftItem;
        // CraftBukkit end
    }

    /**
     * @author TonimatasDEV
     * @reason CraftBukkit
     */
    @Overwrite
    private static boolean tryShearLivingEntity(ServerLevel p_123583_, BlockPos p_123584_) {
        for(LivingEntity livingentity : p_123583_.getEntitiesOfClass(LivingEntity.class, new AABB(p_123584_), EntitySelector.NO_SPECTATORS)) {
            if (livingentity instanceof Shearable shearable) {
                if (shearable.readyForShearing()) {
                    // CraftBukkit start
                    if (CraftEventFactory.callBlockShearEntityEvent(livingentity, cerium$bukkitBlock, cerium$craftItem).isCancelled()) {
                        continue;
                    }
                    // CraftBukkit end
                    shearable.shear(SoundSource.BLOCKS);
                    p_123583_.gameEvent((Entity)null, GameEvent.SHEAR, p_123584_);
                    return true;
                }
            }
        }

        return false;
    }
}
