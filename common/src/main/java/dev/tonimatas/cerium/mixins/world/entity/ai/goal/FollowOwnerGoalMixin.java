package dev.tonimatas.cerium.mixins.world.entity.ai.goal;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R3.event.CraftEventFactory;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(FollowOwnerGoal.class)
public abstract class FollowOwnerGoalMixin {
    @Shadow @Final private TamableAnimal tamable;
    @Shadow private LivingEntity owner;
    @Shadow protected abstract boolean canTeleportTo(BlockPos blockPos);
    @Shadow @Final private PathNavigation navigation;

    /**
     * @author TonimatasDEV
     * @reason CraftBukkit
     */
    @Overwrite
    private boolean maybeTeleportTo(int i, int j, int k) {
        if (Math.abs((double)i - this.owner.getX()) < 2.0 && Math.abs((double)k - this.owner.getZ()) < 2.0) {
            return false;
        } else if (!this.canTeleportTo(new BlockPos(i, j, k))) {
            return false;
        } else {
            // CraftBukkit start
            EntityTeleportEvent event = CraftEventFactory.callEntityTeleportEvent(this.tamable, (double) i + 0.5D, (double) j, (double) k + 0.5D);
            if (event.isCancelled()) {
                return false;
            }
            Location to = event.getTo();
            this.tamable.moveTo(to.getX(), to.getY(), to.getZ(), to.getYaw(), to.getPitch());
            // CraftBukkit end
            this.navigation.stop();
            return true;
        }
    }
}
