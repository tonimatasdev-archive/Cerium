package dev.tonimatas.cerium.mixins.world.entity.ai.goal;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.RemoveBlockGoal;
import net.minecraft.world.level.Level;
import org.bukkit.craftbukkit.v1_20_R3.block.CraftBlock;
import org.bukkit.craftbukkit.v1_20_R3.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RemoveBlockGoal.class)
public class RemoveBlockGoalMixin {
    @Shadow @Final private Mob removerMob;

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;removeBlock(Lnet/minecraft/core/BlockPos;Z)Z"), cancellable = true)
    private void cerium$tick(CallbackInfo ci, @Local Level level, @Local BlockPos blockPos, @Local BlockPos blockPos2) {
        // CraftBukkit start - Step on eggs
        if (!CraftEventFactory.callEntityInteractEvent(this.removerMob, CraftBlock.at(level, blockPos2))) {
            ci.cancel();
        }
        // CraftBukkit end
    }
}
