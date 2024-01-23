package dev.tonimatas.cerium.mixins.world.entity.ai.goal;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.RunAroundLikeCrazyGoal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.v1_20_R3.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(RunAroundLikeCrazyGoal.class)
public class RunAroundLikeCrazyGoalMixin {
    @Shadow @Final private AbstractHorse horse;

    /**
     * @author TonimatasDEV
     * @reason CraftBukkit
     */
    @Overwrite
    public void tick() {
        if (!this.horse.isTamed() && this.horse.getRandom().nextInt(((RunAroundLikeCrazyGoal) (Object) this).adjustedTickDelay(50)) == 0) {
            Entity entity = this.horse.getFirstPassenger();
            if (entity == null) {
                return;
            }

            if (entity instanceof Player) {
                Player player = (Player)entity;
                int i = this.horse.getTemper();
                int j = this.horse.getMaxTemper();
                if (j > 0 && this.horse.getRandom().nextInt(j) < i && !CraftEventFactory.callEntityTameEvent(this.horse, ((CraftHumanEntity) this.horse.getBukkitEntity().getPassenger()).getHandle()).isCancelled()) { // CraftBukkit - fire EntityTameEvent
                    this.horse.tameWithName(player);
                    return;
                }

                this.horse.modifyTemper(5);
            }

            this.horse.ejectPassengers();
            this.horse.makeMad();
            this.horse.level().broadcastEntityEvent(this.horse, (byte)6);
        }

    }
}
