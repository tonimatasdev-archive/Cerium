package dev.tonimatas.cerium.mixins.world.level.block;

import dev.tonimatas.cerium.bridge.world.entity.EntityBridge;
import dev.tonimatas.cerium.bridge.world.level.LevelBridge;
import dev.tonimatas.cerium.bridge.world.level.block.BlockBridge;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SculkSensorBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.v1_20_R3.block.CraftBlock;
import org.bukkit.craftbukkit.v1_20_R3.event.CraftEventFactory;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SculkSensorBlock.class)
public abstract class SculkSensorBlockMixin implements BlockBridge {
    @Inject(method = "stepOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBlockEntity(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/entity/BlockEntity;"))
    private void cerium$stepOn(Level level, BlockPos blockPos, BlockState blockState, Entity entity, CallbackInfo ci) {
        // CraftBukkit start
        org.bukkit.event.Cancellable cancellable;
        if (entity instanceof Player) {
            cancellable = CraftEventFactory.callPlayerInteractEvent((Player) entity, org.bukkit.event.block.Action.PHYSICAL, blockPos, null, null, null);
        } else {
            cancellable = new EntityInteractEvent(((EntityBridge) entity).getBukkitEntity(), ((LevelBridge) level).getWorld().getBlockAt(blockPos.getX(), blockPos.getY(), blockPos.getZ()));
            ((LevelBridge) level).getCraftServer().getPluginManager().callEvent((org.bukkit.event.entity.EntityInteractEvent) cancellable);
        }
        if (cancellable.isCancelled()) {
            return;
        }
        // CraftBukkit end
    }
    
    @Inject(method = "deactivate", at = @At(value = "HEAD"))
    private static void cerium$deactivate(Level level, BlockPos blockPos, BlockState blockState, CallbackInfo ci) {
        // CraftBukkit start
        BlockRedstoneEvent eventRedstone = new BlockRedstoneEvent(CraftBlock.at(level, blockPos), blockState.getValue(SculkSensorBlock.POWER), 0);
        ((LevelBridge) level).getCraftServer().getPluginManager().callEvent(eventRedstone);

        if (eventRedstone.getNewCurrent() > 0) {
            level.setBlock(blockPos, blockState.setValue(SculkSensorBlock.POWER, eventRedstone.getNewCurrent()), 3);
            return;
        }
        // CraftBukkit end
    }
    
    @Inject(method = "activate", at = @At(value = "HEAD"))
    private void cerium$activate(Entity entity, Level level, BlockPos blockPos, BlockState blockState, int i, int j, CallbackInfo ci) {
        // CraftBukkit start
        BlockRedstoneEvent eventRedstone = new BlockRedstoneEvent(CraftBlock.at(level, blockPos), blockState.getValue(SculkSensorBlock.POWER), i);
        ((LevelBridge) level).getCraftServer().getPluginManager().callEvent(eventRedstone);

        if (eventRedstone.getNewCurrent() <= 0) {
            return;
        }
        i = eventRedstone.getNewCurrent();
        // CraftBukkit end
    }
    
    @Inject(method = "spawnAfterBreak", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/BaseEntityBlock;spawnAfterBreak(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/item/ItemStack;Z)V", shift = At.Shift.AFTER), cancellable = true)
    private void cerium$spawnAfterBreak(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, ItemStack itemStack, boolean bl, CallbackInfo ci) {
        ci.cancel();
    }

    @Override
    public int getExpDrop(BlockState iblockdata, ServerLevel worldserver, BlockPos blockposition, ItemStack itemstack, boolean flag) {
        if (flag) {
            return this.tryDropExperience(worldserver, blockposition, itemstack, ConstantInt.of(5));
        }

        return 0;
        // CraftBukkit end
    }
}
