package dev.tonimatas.cerium.mixins.server.commands;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.tonimatas.cerium.bridge.world.entity.EntityBridge;
import dev.tonimatas.cerium.util.CeriumEventFactory;
import dev.tonimatas.cerium.util.Hooks;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.server.commands.TeleportCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.level.Level;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Set;

@Mixin(TeleportCommand.class)
public class TeleportCommandMixin {
    /**
     * @author TonimatasDEV
     * @reason CraftBukkit
     */
    @Overwrite
    private static void performTeleport(CommandSourceStack commandSourceStack, Entity entity, ServerLevel serverLevel, double d, double e, double f, Set<RelativeMovement> set, float g, float h, @Nullable TeleportCommand.LookAt lookAt) throws CommandSyntaxException {
        // Cerium start - Add forge/neoforge event
        if (Hooks.isNeoForge() || Hooks.isForge()) {
            CeriumEventFactory.TeleportCommand event = CeriumEventFactory.onEntityTeleportCommand(entity, d, e, f);
            if (event.cancelled()) return;
            d = event.targetX();
            e = event.targetY();
            f = event.targetZ();
        }
        // Cerium end

        BlockPos blockPos = BlockPos.containing(d, e, f);
        if (!Level.isInSpawnableBounds(blockPos)) {
            throw TeleportCommand.INVALID_POSITION.create();
        } else {
            float i = Mth.wrapDegrees(g);
            float j = Mth.wrapDegrees(h);
            // CraftBukkit start - Teleport event
            boolean result;
            if (entity instanceof ServerPlayer player) {
                result = player.teleportTo(serverLevel, d, e, f, set, i, j, PlayerTeleportEvent.TeleportCause.COMMAND);
            } else {
                Location to = new Location(serverLevel.getWorld(), d, e, f, i, j);
                EntityTeleportEvent event = new EntityTeleportEvent(((EntityBridge) entity).getBukkitEntity(), ((EntityBridge) entity).getBukkitEntity().getLocation(), to);
                serverLevel.getCraftServer().getPluginManager().callEvent(event);
                if (event.isCancelled()) {
                    return;
                }

                d = to.getX();
                e = to.getY();
                f = to.getZ();
                i = to.getYaw();
                j = to.getPitch();
                serverLevel = ((CraftWorld) to.getWorld()).getHandle();

                result = entity.teleportTo(serverLevel, d, e, f, set, i, j);
            }

            if (result) {
                // CraftBukkit end
                if (lookAt != null) {
                    lookAt.perform(commandSourceStack, entity);
                }

                label23: {
                    if (entity instanceof LivingEntity) {
                        LivingEntity livingEntity = (LivingEntity)entity;
                        if (livingEntity.isFallFlying()) {
                            break label23;
                        }
                    }

                    entity.setDeltaMovement(entity.getDeltaMovement().multiply(1.0, 0.0, 1.0));
                    entity.setOnGround(true);
                }

                if (entity instanceof PathfinderMob) {
                    PathfinderMob pathfinderMob = (PathfinderMob)entity;
                    pathfinderMob.getNavigation().stop();
                }

            }
        }
    }
}
