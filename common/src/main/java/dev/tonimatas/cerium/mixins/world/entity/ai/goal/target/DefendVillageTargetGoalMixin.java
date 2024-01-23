package dev.tonimatas.cerium.mixins.world.entity.ai.goal.target;

import net.minecraft.world.entity.ai.goal.target.DefendVillageTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import org.bukkit.event.entity.EntityTargetEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DefendVillageTargetGoal.class)
public class DefendVillageTargetGoalMixin {
    @Shadow @Final private IronGolem golem;

    @Inject(method = "start", at = @At(value = "HEAD"))
    private void cerium$start(CallbackInfo ci) {
        this.golem.setTargetCause(EntityTargetEvent.TargetReason.DEFEND_VILLAGE, true); // Cerium // CraftBukkit - Reason
    }
}
