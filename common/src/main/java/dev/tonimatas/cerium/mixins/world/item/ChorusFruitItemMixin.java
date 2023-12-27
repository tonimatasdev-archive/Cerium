package dev.tonimatas.cerium.mixins.world.item;

import dev.tonimatas.cerium.util.CeriumEventFactory;
import dev.tonimatas.cerium.util.Hooks;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ChorusFruitItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Optional;

@Mixin(ChorusFruitItem.class)
public class ChorusFruitItemMixin extends Item {
    public ChorusFruitItemMixin(Properties properties) {
        super(properties);
    }

    /**
     * @author TonimatasDEV
     * @reason CraftBukkit
     */
    @Overwrite
    public ItemStack finishUsingItem(ItemStack arg, Level arg2, LivingEntity arg3) {
        ItemStack itemstack = super.finishUsingItem(arg, arg2, arg3);
        if (!arg2.isClientSide) {
            for(int i = 0; i < 16; ++i) {
                double d0 = arg3.getX() + (arg3.getRandom().nextDouble() - 0.5) * 16.0;
                double d1 = Mth.clamp(arg3.getY() + (double)(arg3.getRandom().nextInt(16) - 8), (double)arg2.getMinBuildHeight(), (double)(arg2.getMinBuildHeight() + ((ServerLevel)arg2).getLogicalHeight() - 1));
                double d2 = arg3.getZ() + (arg3.getRandom().nextDouble() - 0.5) * 16.0;
                if (arg3.isPassenger()) {
                    arg3.stopRiding();
                }

                Vec3 vec3 = arg3.position();
                CeriumEventFactory.ChorusFruit event = null;
                if (Hooks.isForge() || Hooks.isNeoForge()) {
                    event = CeriumEventFactory.onChorusFruitTeleport(arg3, d0, d1, d2);
                    if (event.cancelled()) {
                        return itemstack;
                    }
                }

                Optional<Boolean> status;
                if (event != null) {
                    status = arg3.randomTeleport(event.targetX(), event.targetY(), event.targetZ(), true, org.bukkit.event.player.PlayerTeleportEvent.TeleportCause.CHORUS_FRUIT);
                } else {
                    status = arg3.randomTeleport(d0, d1, d2, true, org.bukkit.event.player.PlayerTeleportEvent.TeleportCause.CHORUS_FRUIT);
                }

                if (!status.isPresent()) {
                    break;
                }

                if (status.get()) {
                    // CraftBukkit end
                    arg2.gameEvent(GameEvent.TELEPORT, vec3, GameEvent.Context.of(arg3));
                    SoundSource soundsource;
                    SoundEvent soundevent;
                    if (arg3 instanceof Fox) {
                        soundevent = SoundEvents.FOX_TELEPORT;
                        soundsource = SoundSource.NEUTRAL;
                    } else {
                        soundevent = SoundEvents.CHORUS_FRUIT_TELEPORT;
                        soundsource = SoundSource.PLAYERS;
                    }

                    arg2.playSound((Player)null, arg3.getX(), arg3.getY(), arg3.getZ(), soundevent, soundsource);
                    arg3.resetFallDistance();
                    break;
                }
            }

            if (arg3 instanceof Player) {
                Player player = (Player)arg3;
                player.getCooldowns().addCooldown(this, 20);
            }
        }

        return itemstack;
    }
}
