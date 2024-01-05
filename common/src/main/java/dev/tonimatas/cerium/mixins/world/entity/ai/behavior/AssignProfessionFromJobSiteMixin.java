package dev.tonimatas.cerium.mixins.world.entity.ai.behavior;

import net.minecraft.core.GlobalPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.ai.behavior.AssignProfessionFromJobSite;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftVillager;
import org.bukkit.craftbukkit.v1_20_R3.event.CraftEventFactory;
import org.bukkit.event.entity.VillagerCareerChangeEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Optional;

@Mixin(AssignProfessionFromJobSite.class)
public class AssignProfessionFromJobSiteMixin {
    /**
     * @author TonimatasDEV
     * @reason CraftBukkit
     */
    @Overwrite
    public static BehaviorControl<Villager> create() {
        return BehaviorBuilder.create((instance) -> {
            return instance.group(instance.present(MemoryModuleType.POTENTIAL_JOB_SITE), instance.registered(MemoryModuleType.JOB_SITE)).apply(instance, (memoryAccessor, memoryAccessor2) -> {
                return (serverLevel, villager, l) -> {
                    GlobalPos globalPos = (GlobalPos)instance.get(memoryAccessor);
                    if (!globalPos.pos().closerToCenterThan(villager.position(), 2.0) && !villager.assignProfessionWhenSpawned()) {
                        return false;
                    } else {
                        memoryAccessor.erase();
                        memoryAccessor2.set(globalPos);
                        serverLevel.broadcastEntityEvent(villager, (byte)14);
                        if (villager.getVillagerData().getProfession() != VillagerProfession.NONE) {
                            return true;
                        } else {
                            MinecraftServer minecraftServer = serverLevel.getServer();
                            Optional.ofNullable(minecraftServer.getLevel(globalPos.dimension())).flatMap((serverLevelx) -> {
                                return serverLevelx.getPoiManager().getType(globalPos.pos());
                            }).flatMap((holder) -> {
                                return BuiltInRegistries.VILLAGER_PROFESSION.stream().filter((villagerProfession) -> {
                                    return villagerProfession.heldJobSite().test(holder);
                                }).findFirst();
                            }).ifPresent((villagerProfession) -> {
                                // CraftBukkit start - Fire VillagerCareerChangeEvent where Villager gets employed
                                VillagerCareerChangeEvent event = CraftEventFactory.callVillagerCareerChangeEvent(villager, CraftVillager.CraftProfession.minecraftToBukkit(villagerProfession), VillagerCareerChangeEvent.ChangeReason.EMPLOYED);
                                if (event.isCancelled()) {
                                    return;
                                }

                                villager.setVillagerData(villager.getVillagerData().setProfession(CraftVillager.CraftProfession.bukkitToMinecraft(event.getProfession())));
                                // CraftBukkit end
                                villager.refreshBrain(serverLevel);
                            });
                            return true;
                        }
                    }
                };
            });
        });
    }
}
