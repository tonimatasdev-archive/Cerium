package dev.tonimatas.cerium.mixins.server.players;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.SleepStatus;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Iterator;
import java.util.List;

@Mixin(SleepStatus.class)
public abstract class SleepStatusMixin {
    @Shadow public abstract int sleepersNeeded(int i);

    @Shadow private int activePlayers;

    @Shadow private int sleepingPlayers;

    /**
     * @author TonimatasDEV
     * @reason CraftBukkit
     */
    @Overwrite
    public boolean areEnoughDeepSleeping(int i, List<ServerPlayer> list) {
        // CraftBukkit start
        int j = (int) list.stream().filter((eh) -> { return eh.isSleepingLongEnough() || eh.fauxSleeping; }).count();
        boolean anyDeepSleep = list.stream().anyMatch(Player::isSleepingLongEnough);
        return anyDeepSleep && j >= this.sleepersNeeded(i);
        // CraftBukkit end
    }
    
    /**
     * @author TonimatasDEV
     * @reason CraftBukkit
     */
    @Overwrite
    public boolean update(List<ServerPlayer> list) {
        int i = this.activePlayers;
        int j = this.sleepingPlayers;
        this.activePlayers = 0;
        this.sleepingPlayers = 0;
        Iterator var4 = list.iterator();
        boolean anySleep = false; // CraftBukkit

        while(var4.hasNext()) {
            ServerPlayer serverPlayer = (ServerPlayer)var4.next();
            if (!serverPlayer.isSpectator()) {
                ++this.activePlayers;
                if (serverPlayer.isSleeping() || serverPlayer.fauxSleeping) { // CraftBukkit
                    ++this.sleepingPlayers;
                }
                // CraftBukkit start
                if (serverPlayer.isSleeping()) {
                    anySleep = true;
                }
                // CraftBukkit end
            }
        }

        return anySleep && (j > 0 || this.sleepingPlayers > 0) && (i != this.activePlayers || j != this.sleepingPlayers); // CraftBukkit
    }
}
