package dev.tonimatas.cerium.mixins.world.effect;

import dev.tonimatas.cerium.bridge.world.entity.LivingEntityBridge;
import dev.tonimatas.cerium.util.CeriumValues;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.List;

@Mixin(MobEffectUtil.class)
public class MobEffectUtilMixin {
    /**
     * @author TonimatasDEV
     * @reason CraftBukkit
     */
    @Overwrite
    public static List<ServerPlayer> addEffectToPlayersAround(ServerLevel serverLevel, @Nullable Entity entity, Vec3 vec3, double d, MobEffectInstance mobEffectInstance, int i) {
        MobEffect mobEffect = mobEffectInstance.getEffect();
        List<ServerPlayer> list = serverLevel.getPlayers((serverPlayer) -> {
            return serverPlayer.gameMode.isSurvival() && (entity == null || !entity.isAlliedTo(serverPlayer)) && vec3.closerThan(serverPlayer.position(), d) && (!serverPlayer.hasEffect(mobEffect) || serverPlayer.getEffect(mobEffect).getAmplifier() < mobEffectInstance.getAmplifier() || serverPlayer.getEffect(mobEffect).endsWithin(i - 1));
        });
        list.forEach((serverPlayer) -> {
            ((LivingEntityBridge) serverPlayer).cerium$addEffectCause(CeriumValues.potionEffectCause.getAndSet(EntityPotionEffectEvent.Cause.UNKNOWN));
            serverPlayer.addEffect(new MobEffectInstance(mobEffectInstance));
        });
        return list;
    }
}
