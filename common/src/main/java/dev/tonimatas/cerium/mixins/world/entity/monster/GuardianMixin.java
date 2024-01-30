package dev.tonimatas.cerium.mixins.world.entity.monster;

import dev.tonimatas.cerium.bridge.world.entity.monster.GuardianBridge;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.monster.Guardian;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Guardian.class)
public class GuardianMixin implements GuardianBridge {
    @Unique public Guardian.GuardianAttackGoal guardianAttackGoal; // CraftBukkit - add field

    @Override
    public Guardian.GuardianAttackGoal cerium$getGoal() {
        return guardianAttackGoal;
    }
    
    @Redirect(method = "registerGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/goal/GoalSelector;addGoal(ILnet/minecraft/world/entity/ai/goal/Goal;)V", ordinal = 0))
    private void cerium$init(GoalSelector instance, int i, Goal goal) {
        instance.addGoal(4, guardianAttackGoal = new Guardian.GuardianAttackGoal((Guardian) (Object) this)); // CraftBukkit - assign field);
    }
}
