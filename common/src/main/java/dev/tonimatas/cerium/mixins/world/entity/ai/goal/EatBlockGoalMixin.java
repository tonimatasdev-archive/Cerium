package dev.tonimatas.cerium.mixins.world.entity.ai.goal;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.EatBlockGoal;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import org.bukkit.craftbukkit.v1_20_R3.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EatBlockGoal.class)
public class EatBlockGoalMixin {
    @Shadow @Final private Mob mob;

    @Shadow @Final private Level level;

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/GameRules;getBoolean(Lnet/minecraft/world/level/GameRules$Key;)Z", ordinal = 0))
    private boolean cerium$tick$1(GameRules instance, GameRules.Key<GameRules.BooleanValue> arg, @Local BlockPos blockposition) {
        return CraftEventFactory.callEntityChangeBlockEvent(this.mob, blockposition, Blocks.AIR.defaultBlockState(), !this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING));
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/GameRules;getBoolean(Lnet/minecraft/world/level/GameRules$Key;)Z", ordinal = 1))
    private boolean cerium$tick$2(GameRules instance, GameRules.Key<GameRules.BooleanValue> arg, @Local(ordinal = 1) BlockPos blockposition1) {
        return CraftEventFactory.callEntityChangeBlockEvent(this.mob, blockposition1, Blocks.AIR.defaultBlockState(), !this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING));
    }
}
