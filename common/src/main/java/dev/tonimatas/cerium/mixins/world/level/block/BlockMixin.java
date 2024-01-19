package dev.tonimatas.cerium.mixins.world.level.block;

import dev.tonimatas.cerium.bridge.world.level.block.BlockBridge;
import dev.tonimatas.cerium.util.CeriumValues;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.event.entity.EntityExhaustionEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public class BlockMixin implements BlockBridge {
    @Redirect(method = "popResource(Lnet/minecraft/world/level/Level;Ljava/util/function/Supplier;Lnet/minecraft/world/item/ItemStack;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private static boolean cerium$popResource(Level instance, Entity entity) {
        // CraftBukkit start
        if (instance.captureDrops != null) {
            instance.captureDrops.add(entity);
        } else {
            instance.addFreshEntity(entity);
        }
        // CraftBukkit end
        
        return true;
    }
    
    @Inject(method = "playerDestroy", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;causeFoodExhaustion(F)V"))
    private void cerium$playerDestroy(Level level, Player player, BlockPos blockPos, BlockState blockState, BlockEntity blockEntity, ItemStack itemStack, CallbackInfo ci) {
        CeriumValues.exhaustionReason.set(EntityExhaustionEvent.ExhaustionReason.BLOCK_MINED);
    }
    
    @Override
    @Unique
    public int tryDropExperience(ServerLevel serverLevel, BlockPos blockPos, ItemStack itemStack, IntProvider intProvider) {
        if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, itemStack) == 0) {
            int i = intProvider.sample(serverLevel.random);
            if (i > 0) {
                // this.popExperience(serverLevel, blockPos, i);
                return i;
            }
        }

        return 0;
    }

    @Override
    @Unique
    public int getExpDrop(BlockState iblockdata, ServerLevel worldserver, BlockPos blockposition, ItemStack itemstack, boolean flag) {
        return 0;
    }
    // CraftBukkit end
}
