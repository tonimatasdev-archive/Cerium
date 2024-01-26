package dev.tonimatas.cerium.mixins.world.entity.item;

import dev.tonimatas.cerium.bridge.world.level.LevelBridge;
import dev.tonimatas.cerium.util.CeriumValues;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Fallable;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.v1_20_R3.event.CraftEventFactory;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.function.Predicate;

@Mixin(FallingBlockEntity.class)
public abstract class FallingBlockEntityMixin {
    @Shadow private BlockState blockState;
    @Shadow public boolean hurtEntities;
    @Shadow public float fallDamagePerDistance;
    @Shadow public int fallDamageMax;
    @Shadow public boolean cancelDrop;

    @Unique
    private static FallingBlockEntity fall(Level world, BlockPos blockposition, BlockState iblockdata, CreatureSpawnEvent.SpawnReason spawnReason) {
        CeriumValues.spawnReason.set(spawnReason);
        return FallingBlockEntity.fall(world, blockposition, iblockdata);
    }
    
    @Inject(method = "fall", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private static void cerium$fall$1(Level level, BlockPos blockPos, BlockState blockState, CallbackInfoReturnable<FallingBlockEntity> cir, FallingBlockEntity fallingBlockEntity) {
        if (!CraftEventFactory.callEntityChangeBlockEvent(fallingBlockEntity, blockPos, blockState.getFluidState().createLegacyBlock())) cir.setReturnValue(fallingBlockEntity); // CraftBukkit
    }
    
    @Inject(method = "fall", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private static void cerium$fall$2(Level level, BlockPos blockPos, BlockState blockState, CallbackInfoReturnable<FallingBlockEntity> cir) {
        ((LevelBridge) level).cerium$addFreshEntityReason(CeriumValues.spawnReason.getAndSet(CreatureSpawnEvent.SpawnReason.DEFAULT));
    }
    
    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void cerium$tick$1(CallbackInfo ci, Block block, BlockPos blockPos) {
        // CraftBukkit start
        if (!CraftEventFactory.callEntityChangeBlockEvent((FallingBlockEntity) (Object) this, blockPos, this.blockState)) {
            ((FallingBlockEntity) (Object) this).discard(); // SPIGOT-6586 called before the event in previous versions
            ci.cancel();
        }
        // CraftBukkit end
    }
    
    /**
     * @author TonimatasDEV
     * @reason CraftBukkit
     */
    @Overwrite
    public boolean causeFallDamage(float f, float g, DamageSource damageSource) {
        if (!this.hurtEntities) {
            return false;
        } else {
            int i = Mth.ceil(f - 1.0F);
            if (i < 0) {
                return false;
            } else {
                Predicate<Entity> predicate = EntitySelector.NO_CREATIVE_OR_SPECTATOR.and(EntitySelector.LIVING_ENTITY_STILL_ALIVE);
                Block var8 = this.blockState.getBlock();
                DamageSource var10000;
                if (var8 instanceof Fallable) {
                    Fallable fallable = (Fallable)var8;
                    var10000 = fallable.getFallDamageSource(((FallingBlockEntity) (Object) this));
                } else {
                    var10000 = ((FallingBlockEntity) (Object) this).damageSources().fallingBlock(((FallingBlockEntity) (Object) this));
                }

                DamageSource damageSource2 = var10000;
                float h = (float)Math.min(Mth.floor((float)i * this.fallDamagePerDistance), this.fallDamageMax);
                ((FallingBlockEntity) (Object) this).level().getEntities(((FallingBlockEntity) (Object) this), ((FallingBlockEntity) (Object) this).getBoundingBox(), predicate).forEach((entity) -> {
                    CraftEventFactory.entityDamage = ((FallingBlockEntity) (Object) this); // CraftBukkit
                    entity.hurt(damageSource2, h);
                    CraftEventFactory.entityDamage = null; // CraftBukkit
                });
                boolean bl = this.blockState.is(BlockTags.ANVIL);
                if (bl && h > 0.0F && ((FallingBlockEntity) (Object) this).random.nextFloat() < 0.05F + (float)i * 0.05F) {
                    BlockState blockState = AnvilBlock.damage(this.blockState);
                    if (blockState == null) {
                        this.cancelDrop = true;
                    } else {
                        this.blockState = blockState;
                    }
                }

                return false;
            }
        }
    }
}
