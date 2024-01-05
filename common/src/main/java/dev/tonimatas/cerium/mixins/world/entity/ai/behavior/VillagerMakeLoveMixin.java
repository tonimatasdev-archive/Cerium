package dev.tonimatas.cerium.mixins.world.entity.ai.behavior;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.VillagerMakeLove;
import net.minecraft.world.entity.npc.Villager;
import org.bukkit.craftbukkit.v1_20_R3.event.CraftEventFactory;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Optional;

@Mixin(VillagerMakeLove.class)
public class VillagerMakeLoveMixin {
    /**
     * @author TonimatasDEV
     * @reason CraftBukkit
     */
    @Overwrite
    private Optional<Villager> breed(ServerLevel serverLevel, Villager villager, Villager villager2) {
        Villager villager3 = villager.getBreedOffspring(serverLevel, villager2);
        if (villager3 == null) {
            return Optional.empty();
        } else {
            villager.setAge(6000);
            villager2.setAge(6000);
            villager3.setAge(-24000);
            villager3.moveTo(villager.getX(), villager.getY(), villager.getZ(), 0.0F, 0.0F);
            // CraftBukkit start - call EntityBreedEvent
            if (CraftEventFactory.callEntityBreedEvent(villager3, villager, villager2, null, null, 0).isCancelled()) {
                return Optional.empty();
            }
            // Move age setting down
            villager.setAge(6000);
            villager2.setAge(6000);
            serverLevel.addFreshEntityWithPassengersCause(CreatureSpawnEvent.SpawnReason.BREEDING);
            serverLevel.addFreshEntityWithPassengers(villager3);
            // CraftBukkit end
            serverLevel.addFreshEntityWithPassengers(villager3);
            serverLevel.broadcastEntityEvent(villager3, (byte)12);
            return Optional.of(villager3);
        }
    }
}
