package dev.tonimatas.cerium.mixins.world.entity;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Interaction;
import net.minecraft.world.entity.player.Player;
import org.bukkit.craftbukkit.v1_20_R3.event.CraftEventFactory;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Interaction.class)
public abstract class InteractionMixin {
    @Shadow @Nullable public Interaction.PlayerAction attack;

    @Shadow public abstract boolean getResponse();

    /**
     * @author TonimatasDEV
     * @reason CraftBukkit
     */
    @Overwrite
    public boolean skipAttackInteraction(Entity entity) {
        if (entity instanceof Player player) {
            // CraftBukkit start
            DamageSource source = player.damageSources().playerAttack(player);
            EntityDamageEvent event = CraftEventFactory.callNonLivingEntityDamageEvent((Interaction) (Object) this, source, 1.0F, false);
            if (event.isCancelled()) {
                return true;
            }
            // CraftBukkit end
            
            this.attack = new Interaction.PlayerAction(player.getUUID(), ((Interaction) (Object) this).level().getGameTime());
            if (player instanceof ServerPlayer serverPlayer) {
                CriteriaTriggers.PLAYER_HURT_ENTITY.trigger(serverPlayer, (Interaction) (Object) this, source, (float) event.getFinalDamage(), 1.0F, false); // CraftBukkit
            }

            return !this.getResponse();
        } else {
            return false;
        }
    }
}
