package dev.tonimatas.cerium.mixins.world.entity.ai.sensing;


import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.TemptingSensor;
import net.minecraft.world.entity.player.Player;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.v1_20_R3.event.CraftEventFactory;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TemptingSensor.class)
public class TemptingSensorMixin {
    @Redirect(method = "doTick(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/PathfinderMob;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/Brain;setMemory(Lnet/minecraft/world/entity/ai/memory/MemoryModuleType;Ljava/lang/Object;)V"))
    private <U> void cerium$doTick(Brain instance, MemoryModuleType<U> arg, U object, @Local PathfinderMob pathfinderMob, @Local Player player) {
        // CraftBukkit start
        EntityTargetLivingEntityEvent event = CraftEventFactory.callEntityTargetLivingEvent(pathfinderMob, player, EntityTargetEvent.TargetReason.TEMPT);
        if (event.isCancelled()) {
            return;
        }
        if (event.getTarget() instanceof Player) {
            instance.setMemory(MemoryModuleType.TEMPTING_PLAYER, ((CraftHumanEntity) event.getTarget()).getHandle());
        } else {
            instance.eraseMemory(MemoryModuleType.TEMPTING_PLAYER);
        }
        // CraftBukkit end
    }
}
