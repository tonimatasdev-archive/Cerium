package dev.tonimatas.cerium.mixins.world.entity.ai.behavior;

import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.ResetProfession;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerProfession;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftVillager;
import org.bukkit.craftbukkit.v1_20_R3.event.CraftEventFactory;
import org.bukkit.event.entity.VillagerCareerChangeEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(ResetProfession.class)
public class ResetProfessionMixin {
    /**
     * @author TonimatasDEV
     * @reason CraftBukkit
     */
    @Overwrite
    public static BehaviorControl<Villager> create() {
        return BehaviorBuilder.create((instance) -> {
            return instance.group(instance.absent(MemoryModuleType.JOB_SITE)).apply(instance, (memoryAccessor) -> {
                return (serverLevel, villager, l) -> {
                    VillagerData villagerData = villager.getVillagerData();
                    if (villagerData.getProfession() != VillagerProfession.NONE && villagerData.getProfession() != VillagerProfession.NITWIT && villager.getVillagerXp() == 0 && villagerData.getLevel() <= 1) {
                        // CraftBukkit start
                        VillagerCareerChangeEvent event = CraftEventFactory.callVillagerCareerChangeEvent(villager, CraftVillager.CraftProfession.minecraftToBukkit(VillagerProfession.NONE), VillagerCareerChangeEvent.ChangeReason.LOSING_JOB);
                        if (event.isCancelled()) {
                            return false;
                        }

                        villager.setVillagerData(villager.getVillagerData().setProfession(CraftVillager.CraftProfession.bukkitToMinecraft(event.getProfession())));
                        // CraftBukkit end
                        villager.refreshBrain(serverLevel);
                        return true;
                    } else {
                        return false;
                    }
                };
            });
        });
    }
}
